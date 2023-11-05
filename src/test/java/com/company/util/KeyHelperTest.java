package com.company.util;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static com.company.util.KeyHelper.getPrivateKey;
import static com.company.util.KeyHelper.getPublicKey;
import static org.assertj.core.api.Assertions.assertThat;

class KeyHelperTest {
    @Test
    void testEncodeDecode() throws NoSuchAlgorithmException {
        KeyPairRecord record = KeyHelper.createKeyPair();
        byte[] pub = record.publicKey();
        byte[] priv = record.privateKey();

        assertThat(getPublicKey(pub).getEncoded()).isEqualTo(pub);
        assertThat(getPrivateKey(priv).getEncoded()).isEqualTo(priv);

    }

}