package com.company.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.type.descriptor.java.ByteArrayJavaType;

import java.io.Serializable;
import java.security.*;

@Entity
@Data
@NoArgsConstructor
public class Wallet {
    @Id
    private Long id;

    private byte[] publicKey;

    private byte[] privateKey;

    public Wallet(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }


}