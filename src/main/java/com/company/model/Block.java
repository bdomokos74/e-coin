package com.company.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static com.company.util.KeyHelper.getPublicKey;

@Entity
@Table(name = "BLOCKCHAIN")
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@AllArgsConstructor
public class Block implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BLOCKCHAIN")
    @SequenceGenerator(name = "SEQ_BLOCKCHAIN", sequenceName = "SEQ_BLOCKCHAIN", allocationSize = 1)
    @Column(name = "LEDGER_ID")
    private Long ledgerId; // = 1;

    @Column(name = "PREVIOUS_HASH")
    @EqualsAndHashCode.Include
    private byte[] prevHash;
    @Column(name = "CURRENT_HASH", length = 2048)
    private byte[] currHash;
    @Column(name = "CREATED_ON", length = 2048)
    private String timeStamp;
    @Column(name = "CREATED_BY", length = 2048)
    private byte[] minedBy;

    @Column(name = "MINING_POINTS")
    private Integer miningPoints = 0;
    @Column(name = "LUCK")
    private Double luck = 0.0;

    @OneToMany(targetEntity = Transaction.class,
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    @JoinColumn(name = "LEDGER_ID")
    private List<Transaction> transactionLedger = new ArrayList<>();

    //This constructor is used when we retrieve it from the db
    public Block(byte[] prevHash, byte[] currHash, String timeStamp, byte[] minedBy,Long ledgerId,
                 Integer miningPoints, Double luck, ArrayList<Transaction> transactionLedger) {
        this.prevHash = prevHash;
        this.currHash = currHash;
        this.timeStamp = timeStamp;
        this.minedBy = minedBy;
        this.ledgerId = ledgerId;
        this.miningPoints = miningPoints;
        this.luck = luck;
        this.transactionLedger = transactionLedger;
    }
    //This constructor is used when we initiate it after retrieve.
    public Block(LinkedList<Block> currentBlockChain) {
        Block lastBlock = currentBlockChain.getLast();
        prevHash = lastBlock.getCurrHash();
        ledgerId = lastBlock.getLedgerId() + 1;
        luck = Math.random() * 1000000;
    }
    //This constructor is used only for creating the first block in the blockchain.
    public Block() {
        prevHash = new byte[]{0};
    }

    public Boolean isVerified(Signature signing)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

        PublicKey publicKey= getPublicKey(this.minedBy);
        signing.initVerify(publicKey);
        signing.update(this.toString().getBytes());
        return signing.verify(this.currHash);
    }

    @Override
    public String toString() {
        return "Block{" +
                "prevHash=" + Arrays.toString(prevHash) +
                ", timeStamp='" + timeStamp + '\'' +
                ", minedBy=" + Arrays.toString(minedBy) +
                ", ledgerId=" + ledgerId +
                ", miningPoints=" + miningPoints +
                ", luck=" + luck +
                '}';
    }

    public void addTransaction(Transaction transaction) {
        transaction.setLedgerId(this.ledgerId);
        transactionLedger.add(transaction);
    }
}
