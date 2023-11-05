package com.company.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

import java.math.BigDecimal;

@Entity(name = "BLOCKCHAIN")
@Data
public class BlockChainRecord {
    @Id
    @Column(name = "ID")
    private Long id;

    @Lob
    @Column(name = "PREVIOUS_HASH")
    private byte[] previousHash;

    @Lob
    @Column(name = "CURRENT_HASH")
    private byte[] currentHash;

    @Column(name = "LEDGER_ID")
    private Long ledgerId;

    @Column(name = "CREATED_ON")
    private String createdOn;

    @Column(name = "CREATED_BY")
    private byte[] createdBy;

    @Column(name = "MINING_POINTS")
    private String miningPoints;

    @Column(name = "LUCK")
    private BigDecimal luck;
}
