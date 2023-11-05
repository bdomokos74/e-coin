package com.company.util;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class KeyHelper {
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
}
