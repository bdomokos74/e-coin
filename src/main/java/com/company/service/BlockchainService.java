package com.company.service;

import com.company.model.Block;
import com.company.model.Transaction;
import com.company.model.Wallet;
import com.company.repository.BlockChainRepository;
import com.company.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Signature;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.company.util.KeyHelper.getPrivateKey;
import static com.company.util.KeyHelper.getPublicKey;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BlockchainService {
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final BlockChainRepository blockChainRepository;

    private final ObservableList<Transaction> newBlockTransactionsFX = FXCollections.observableArrayList();
    private final ObservableList<Transaction> newBlockTransactions = FXCollections.observableArrayList();
    private LinkedList<Block> currentBlockChain = new LinkedList<>();
    private Block latestBlock;
    private boolean exit = false;
    private int miningPoints;
    public static final int TIMEOUT_INTERVAL = 65;
    public static final int MINING_INTERVAL = 60;

    Comparator<Transaction> transactionComparator = Comparator.comparing(Transaction::getTimestamp);

    public ObservableList<Transaction> getTransactionLedgerFX() {
        newBlockTransactionsFX.clear();
        newBlockTransactions.sort(transactionComparator);
        newBlockTransactionsFX.addAll(newBlockTransactions);
        return FXCollections.observableArrayList(newBlockTransactionsFX);
    }

    public String getWalletBallanceFX() {
        Wallet wallet = walletService.loadWallet();
        return getBalance(currentBlockChain, newBlockTransactions, getPublicKey(wallet)).toString();
    }

    public void startBlockChain() {
        try {
            Wallet wallet = walletService.getOrCreateWallet();

            List<Block> blockChainRecords = blockChainRepository.findAll();
            if (blockChainRecords.isEmpty()) {
                Block firstBlock = new Block();
                firstBlock.setMinedBy(getPublicKey(wallet).getEncoded());
                firstBlock.setTimeStamp(LocalDateTime.now().toString());
                firstBlock.setPrevHash( new byte[]{0});
                firstBlock.setLedgerId(1L);
                Signature signing = Signature.getInstance("SHA256withDSA");
                signing.initSign(getPrivateKey(wallet));
                signing.update(firstBlock.toString().getBytes());
                firstBlock.setCurrHash(signing.sign());
                log.info("startBlockChain created first block: ledgerId={} ts={}", firstBlock.getLedgerId(), firstBlock.getTimeStamp());
                Transaction initBlockRewardTransaction = walletService.createTransaction(getPublicKey(wallet).getEncoded(), 100, firstBlock.getLedgerId());
                validateTransaction(initBlockRewardTransaction, true);
                firstBlock.addTransaction(initBlockRewardTransaction);
                addTransactionState(initBlockRewardTransaction);
                blockChainRepository.save(firstBlock);
            }
        } catch (GeneralSecurityException e) {
            log.error("Failed to initialize", e);
        }
        loadBlockChain();
    }

    private Integer getBalance(LinkedList<Block> blockChain, ObservableList<Transaction> currentLedger, PublicKey walletAddress) {
        Integer balance = 0;
        for (Block block : blockChain) {
            for (Transaction transaction : block.getTransactionLedger()) {
                if (Arrays.equals(transaction.getFrom(), walletAddress.getEncoded())) {
                    balance -= transaction.getValue();
                }
                if (Arrays.equals(transaction.getTo(), walletAddress.getEncoded())) {
                    balance += transaction.getValue();
                }
            }
        }
        for (Transaction transaction : currentLedger) {
            if (Arrays.equals(transaction.getFrom(), walletAddress.getEncoded())) {
                balance -= transaction.getValue();
            }
        }
        return balance;
    }

    private void verifyBlockChain(LinkedList<Block> currentBlockChain) throws GeneralSecurityException {
        Signature signing = Signature.getInstance("SHA256withDSA");
        for (Block block : currentBlockChain) {
            if (!block.isVerified(signing)) {
                log.info("failed to validate: {}", block.toReadableString());
                throw new GeneralSecurityException("Block validation failed, block.ledgerId: "+ block.getLedgerId() );
            }
            List<Transaction> transactions = block.getTransactionLedger();
            for (Transaction transaction : transactions) {
                if (!transaction.isVerified(signing)) {
                    throw new GeneralSecurityException("Transaction validation failed, ledgerId: "+ transaction.getLedgerId()+", transactionId: "+transaction.getId());
                }
            }
        }
    }

    public void addTransactionState(Transaction transaction) {
        newBlockTransactions.add(transaction);
        newBlockTransactions.sort(transactionComparator);
    }


    public void validateTransaction(Transaction transaction, boolean blockReward) throws GeneralSecurityException {
        PublicKey pk = getPublicKey(transaction.getFrom());
        Integer balance = getBalance(currentBlockChain, newBlockTransactions, pk);
        if (balance < transaction.getValue() && !blockReward) {
            throw new GeneralSecurityException("Not enough funds by sender to record transaction");
        }
    }

    public void addTransaction(Transaction transaction, boolean blockReward) throws GeneralSecurityException {
        PublicKey pk = getPublicKey(transaction.getFrom());
        Integer balance = getBalance(currentBlockChain, newBlockTransactions, pk);
        if (balance < transaction.getValue() && !blockReward) {
            throw new GeneralSecurityException("Not enough funds by sender to record transaction");
        }

        transactionRepository.save(transaction);

    }

    public void loadBlockChain() {
        try {
            currentBlockChain = new LinkedList<>(blockChainRepository.findAll());

            latestBlock = currentBlockChain.getLast();
            Wallet targetWallet = walletService.getOrCreateWallet();
            Transaction transaction = walletService.createTransactionFromNewWallet(getPublicKey(targetWallet).getEncoded(), 100, latestBlock.getLedgerId() + 1);
            newBlockTransactions.clear();
            newBlockTransactions.add(transaction);
            verifyBlockChain(currentBlockChain);
            log.info("loadBlockChain verifying: {}", currentBlockChain.getLast().getLedgerId());

        } catch (GeneralSecurityException e) {
            log.info("{}", e.getMessage(), e);
        }
    }

    public void mineBlock() {
        try {
            finalizeBlock(walletService.loadWallet());
            addBlock(latestBlock);
        } catch (SQLException | GeneralSecurityException e) {
            log.info("Problem with DB: {}", e.getMessage(), e);
        }
    }

    private void finalizeBlock(Wallet minersWallet) throws GeneralSecurityException, SQLException {
        Signature signing = Signature.getInstance("SHA256withDSA");
        latestBlock = new Block(currentBlockChain);
//        latestBlock.setTransactionLedger(new ArrayList<>(newBlockTransactions));
        newBlockTransactions.forEach(latestBlock::addTransaction);
        latestBlock.setTimeStamp(LocalDateTime.now().toString());
        latestBlock.setMinedBy(getPublicKey(minersWallet).getEncoded());
        latestBlock.setMiningPoints(miningPoints);
        signing.initSign(getPrivateKey(minersWallet));
        signing.update(latestBlock.toString().getBytes());
        latestBlock.setCurrHash(signing.sign());
        log.info("new block: {}", latestBlock.toReadableString());
        currentBlockChain.add(latestBlock);
        miningPoints = 0;
        //Reward transaction
//        latestBlock.getTransactionLedger().sort(transactionComparator);
        addTransaction(latestBlock.getTransactionLedger().get(0), true);
        Transaction transaction = walletService.createTransactionFromNewWallet(getPublicKey(minersWallet).getEncoded(), 100, latestBlock.getLedgerId() + 1);
        newBlockTransactions.clear();
        newBlockTransactions.add(transaction);
    }

    public void addBlock(Block block) {
        blockChainRepository.save(block);
    }

    private void replaceBlockchainInDatabase(LinkedList<Block> receivedBC) throws GeneralSecurityException {
        blockChainRepository.deleteAll();
//        try {
//            Connection connection = DriverManager.getConnection
//                    (getDbPath("blockchain.db"));
//            Statement clearDBStatement = connection.createStatement();
//            clearDBStatement.executeUpdate(" DELETE FROM BLOCKCHAIN ");
//            clearDBStatement.executeUpdate(" DELETE FROM TRANSACTIONS ");
//            clearDBStatement.close();
//            connection.close();
//            for (Block block : receivedBC) {
//                addBlock(block);
//                boolean rewardTransaction = true;
//                block.getTransactionLedger().sort(transactionComparator);
//                for (Transaction transaction : block.getTransactionLedger()) {
//                    addTransaction(transaction, rewardTransaction);
//                    rewardTransaction = false;
//                }
//            }
//        } catch (SQLException | GeneralSecurityException e) {
//            log.info("Problem with DB: {}", e.getMessage(), e);
//        }
        for (Block block : receivedBC) {
            addBlock(block);
            boolean rewardTransaction = true;
            block.getTransactionLedger().sort(transactionComparator);
            for (Transaction transaction : block.getTransactionLedger()) {
                addTransaction(transaction, rewardTransaction);
                rewardTransaction = false;
            }
        }
    }

    public LinkedList<Block> getBlockchainConsensus(LinkedList<Block> receivedBC) {
        try {
            //Verify the validity of the received blockchain.
            log.info("getBlockchainConsensus: verifying received: {}", receivedBC.getLast().getLedgerId());
            verifyBlockChain(receivedBC);
            //Check if we have received an identical blockchain.
            if (!Arrays.equals(receivedBC.getLast().getCurrHash(), getCurrentBlockChain().getLast().getCurrHash())) {
                if (checkIfOutdated(receivedBC) != null) {
                    return getCurrentBlockChain();
                } else {
                    if (checkWhichIsCreatedFirst(receivedBC) != null) {
                        return getCurrentBlockChain();
                    } else {
                        if (compareMiningPointsAndLuck(receivedBC) != null) {
                            return getCurrentBlockChain();
                        }
                    }
                }
                // if only the transaction ledgers are different then combine them.
            } else if (!receivedBC.getLast().getTransactionLedger().equals(getCurrentBlockChain().getLast().getTransactionLedger())) {
                updateTransactionLedgers(receivedBC);
                log.info("Transaction ledgers updated");
                return receivedBC;
            } else {
                log.info("blockchains are identical");
            }
        } catch (GeneralSecurityException e) {
            log.info("getBlockchainConsensus failed to verify received block: {}", e.getMessage(), e);
        }
        return receivedBC;
    }

    private void updateTransactionLedgers(LinkedList<Block> receivedBC) throws GeneralSecurityException {
        for (Transaction transaction : receivedBC.getLast().getTransactionLedger()) {
            if (!getCurrentBlockChain().getLast().getTransactionLedger().contains(transaction)) {
                getCurrentBlockChain().getLast().getTransactionLedger().add(transaction);
                log.info("current ledger id = " + getCurrentBlockChain().getLast().getLedgerId() + " transaction id = " + transaction.getLedgerId());
                addTransaction(transaction, false);
            }
        }
        getCurrentBlockChain().getLast().getTransactionLedger().sort(transactionComparator);
        for (Transaction transaction : getCurrentBlockChain().getLast().getTransactionLedger()) {
            if (!receivedBC.getLast().getTransactionLedger().contains(transaction)) {
                receivedBC.getLast().getTransactionLedger().add(transaction);
            }
        }
        receivedBC.getLast().getTransactionLedger().sort(transactionComparator);
    }

    private LinkedList<Block> checkIfOutdated(LinkedList<Block> receivedBC) throws GeneralSecurityException {
        //Check how old the blockchains are.
        long lastMinedLocalBlock = LocalDateTime.parse
                (getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
        long lastMinedRcvdBlock = LocalDateTime.parse
                (receivedBC.getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
        //if both are old just do nothing
        if ((lastMinedLocalBlock + TIMEOUT_INTERVAL) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) &&
            (lastMinedRcvdBlock + TIMEOUT_INTERVAL) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
            log.info("both are old check other peers");
            //If your blockchain is old but the received one is new use the received one
        } else if ((lastMinedLocalBlock + TIMEOUT_INTERVAL) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) &&
                   (lastMinedRcvdBlock + TIMEOUT_INTERVAL) >= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
            //we reset the mining points since we weren't contributing until now.
            setMiningPoints(0);
            replaceBlockchainInDatabase(receivedBC);
            setCurrentBlockChain(new LinkedList<>());
            loadBlockChain();
            log.info("received blockchain won!, local BC was old");
            //If received one is old but local is new send ours to them
        } else if ((lastMinedLocalBlock + TIMEOUT_INTERVAL) > LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) &&
                   (lastMinedRcvdBlock + TIMEOUT_INTERVAL) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {

            return getCurrentBlockChain();
        }
        return null;
    }

    private LinkedList<Block> checkWhichIsCreatedFirst(LinkedList<Block> receivedBC) throws GeneralSecurityException {
        //Compare timestamps to see which one is created first.
        long initRcvBlockTime = LocalDateTime.parse(receivedBC.getFirst().getTimeStamp())
                .toEpochSecond(ZoneOffset.UTC);
        long initLocalBlockTIme = LocalDateTime.parse(getCurrentBlockChain().getFirst()
                .getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
        if (initRcvBlockTime < initLocalBlockTIme) {
            //we reset the mining points since we weren't contributing until now.
            setMiningPoints(0);
            replaceBlockchainInDatabase(receivedBC);
            setCurrentBlockChain(new LinkedList<>());
            loadBlockChain();
            log.info("PeerClient blockchain won!, PeerServer's BC was old");
        } else if (initLocalBlockTIme < initRcvBlockTime) {
            return getCurrentBlockChain();
        }
        return null;
    }

    private LinkedList<Block> compareMiningPointsAndLuck(LinkedList<Block> receivedBC)
            throws GeneralSecurityException {
        //check if both blockchains have the same prevHashes to confirm they are both
        //contending to mine the last block
        //if they are the same compare the mining points and luck in case of equal mining points
        //of last block to see who wins
        if (receivedBC.equals(getCurrentBlockChain())) {
            //If received block has more mining points or luck in case of tie
            // transfer all transactions to the winning block and add them in DB.
            if (receivedBC.getLast().getMiningPoints() > getCurrentBlockChain()
                    .getLast().getMiningPoints() || receivedBC.getLast().getMiningPoints()
                                                            .equals(getCurrentBlockChain().getLast().getMiningPoints()) &&
                                                    receivedBC.getLast().getLuck().compareTo(getCurrentBlockChain().getLast().getLuck())>0) {
                //remove the reward transaction from our losing block and
                // transfer the transactions to the winning block
                getCurrentBlockChain().getLast().getTransactionLedger().remove(0);
                for (Transaction transaction : getCurrentBlockChain().getLast().getTransactionLedger()) {
                    if (!receivedBC.getLast().getTransactionLedger().contains(transaction)) {
                        receivedBC.getLast().getTransactionLedger().add(transaction);
                    }
                }
                receivedBC.getLast().getTransactionLedger().sort(transactionComparator);
                //we are returning the mining points since our local block lost.
                setMiningPoints(getMiningPoints() + getCurrentBlockChain().getLast().getMiningPoints());
                replaceBlockchainInDatabase(receivedBC);
                setCurrentBlockChain(new LinkedList<>());
                loadBlockChain();
                log.info("Received blockchain won!");
            } else {
                // remove the reward transaction from their losing block and transfer
                // the transactions to our winning block
                receivedBC.getLast().getTransactionLedger().remove(0);
                for (Transaction transaction : receivedBC.getLast().getTransactionLedger()) {
                    if (!getCurrentBlockChain().getLast().getTransactionLedger().contains(transaction)) {
                        getCurrentBlockChain().getLast().getTransactionLedger().add(transaction);
                        addTransaction(transaction, false);
                    }
                }
                getCurrentBlockChain().getLast().getTransactionLedger().sort(transactionComparator);
                return getCurrentBlockChain();
            }
        }
        return null;
    }

    public LinkedList<Block> getCurrentBlockChain() {
        return currentBlockChain;
    }

    public void setCurrentBlockChain(LinkedList<Block> currentBlockChain) {
        this.currentBlockChain = currentBlockChain;
    }


    public int getMiningPoints() {
        return miningPoints;
    }

    public void setMiningPoints(int miningPoints) {
        this.miningPoints = miningPoints;
    }

    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }
}
