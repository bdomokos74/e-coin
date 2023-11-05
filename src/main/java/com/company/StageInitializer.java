package com.company;

import com.company.model.Block;
import com.company.model.BlockChainRecord;
import com.company.model.Transaction;
import com.company.model.Wallet;
import com.company.repository.BlockChainRepository;
import com.company.service.WalletService;
import com.company.servicedata.BlockchainData;
import com.company.threads.MiningThread;
import com.company.threads.PeerClient;
import com.company.threads.PeerServer;
import com.company.threads.UI;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.company.util.KeyHelper.getPrivateKey;
import static com.company.util.KeyHelper.getPublicKey;

@Component
@Slf4j
@RequiredArgsConstructor
public class StageInitializer implements ApplicationListener<ECoin.StageReadyEvent> {
    private final WalletService walletService;
    private final BlockChainRepository blockChainRepository;
    private final BlockchainData blockchainData;
    private final ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ECoin.StageReadyEvent event) {
        log.info("onApplicationEvent");
        startBlockChain();

        Stage stage = event.getStage();
        List<Integer> peerPorts = Arrays.stream(System.getenv("peer.port").split(",")).map(Integer::parseInt).toList();
        int serverPort = Integer.parseInt(System.getenv("server.port"));
        new UI(System.getenv("name"), applicationContext).start(stage);
        new PeerClient(peerPorts, blockchainData).start();
        try {
            new PeerServer(serverPort, blockchainData).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new MiningThread(blockchainData).start();
    }

    private void startBlockChain() {
        try {
            Wallet wallet = walletService.getOrCreateWallet();

            List<BlockChainRecord> blockChainRecords = blockChainRepository.findAll();
            if (blockChainRecords.isEmpty()) {
                Block firstBlock = new Block();
                firstBlock.setMinedBy(getPublicKey(wallet).getEncoded());
                firstBlock.setTimeStamp(LocalDateTime.now().toString());
                //helper class.
                Signature signing = Signature.getInstance("SHA256withDSA");
                signing.initSign(getPrivateKey(wallet));
                signing.update(firstBlock.toString().getBytes());
                firstBlock.setCurrHash(signing.sign());

                Signature transSignature = Signature.getInstance("SHA256withDSA");
                Transaction initBlockRewardTransaction = new Transaction(wallet, getPublicKey(wallet).getEncoded(), 100, 1, transSignature);
                blockchainData.addTransaction(initBlockRewardTransaction, true);
                blockchainData.addTransactionState(initBlockRewardTransaction);
            }
        } catch (GeneralSecurityException e) {
            log.error("Failed to initialize", e);
        }
        blockchainData.loadBlockChain();
    }

}
