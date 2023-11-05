package com.company.model;

import org.junit.jupiter.api.Test;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;
import java.util.Arrays;

import static com.company.util.FileHelper.getDbPath;
class TransactionTest {
    @Test
    void publicKeyFactoryTest() throws Exception {
        System.out.println(Arrays.toString(Security.getProviders()));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(getKeyBytes(), "SHA-256");
        KeyFactory kf = KeyFactory.getInstance("DSA");
        PublicKey pk = kf.generatePublic(spec);
        System.out.println(pk.getFormat());
    }

    @Test
    void signatureFactoryTest() throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(getKeyBytes(), "SHA-256");
    }

    private byte[] getKeyBytes() throws SQLException {
        Connection walletConnection = DriverManager.getConnection(
                getDbPath("wallet.db"));
        Statement walletStatment = walletConnection.createStatement();
        ResultSet resultSet;
        resultSet = walletStatment.executeQuery(" SELECT * FROM WALLET ");
        return resultSet.getBytes("PUBLIC_KEY");
    }
}