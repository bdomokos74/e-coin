package com.company.util;

import com.company.model.Wallet;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyHelper {
    private static final int KEY_SIZE = 2048;

    public static PublicKey getPublicKey(byte[] bytes) {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes, "SHA-256");
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("DSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static PrivateKey getPrivateKey(byte[] bytes) {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes, "SHA-256");
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("DSA");
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static KeyPairRecord createKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return new KeyPairRecord(keyPair.getPublic().getEncoded(), keyPair.getPrivate().getEncoded());
    }

    public static PublicKey getPublicKey(Wallet wallet) {
        return getPublicKey(wallet.getPublicKey()); }
    public static PrivateKey getPrivateKey(Wallet wallet) {
        return getPrivateKey(wallet.getPrivateKey()); }
}
