package com.company.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "WALLET", schema = "ecoin")
public class Wallet {
    @Id
    private Long id;

    @Column(name = "PUBLIC_KEY", length = 2048 )
    private byte[] publicKey;
    @Column(name = "PRIVATE_KEY", length = 2048)
    private byte[] privateKey;

    public Wallet(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}