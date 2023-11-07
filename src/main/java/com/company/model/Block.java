package com.company.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;

import static com.company.util.KeyHelper.getPublicKey;

@Entity(name = "BLOCKCHAIN")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Block implements Serializable {
    @Column(name = "PREVIOUS_HASH")
    @EqualsAndHashCode.Include
    private byte[] prevHash;
    @Column(name = "CURRENT_HASH")
    private byte[] currHash;
    @Column(name = "CREATED_ON")
    private String timeStamp;
    @Column(name = "CREATED_BY")
    private byte[] minedBy;

    @Id
    @GeneratedValue
    @Column(name = "LEDGER_ID")
    private Long ledgerId; // = 1;
    @Column(name = "MINING_POINTS")
    private Integer miningPoints = 0;
    @Column(name = "LUCK")
    private Double luck = 0.0;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "LEDGER_ID")
    private ArrayList<Transaction> transactionLedger;

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
}
