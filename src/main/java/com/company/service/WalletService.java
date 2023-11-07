package com.company.service;

import com.company.model.Transaction;
import com.company.model.Wallet;
import com.company.repository.WalletRepository;
import com.company.util.KeyPairRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.time.LocalDateTime;

import static com.company.util.KeyHelper.*;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WalletService {
    private final WalletRepository walletRepository;
    public Wallet getOrCreateWallet() {
        return walletRepository.findById(1L).orElseGet(() -> {
            try {
                KeyPairRecord keyPairRecord = createKeyPair();
                Wallet wallet = new Wallet(keyPairRecord.publicKey(), keyPairRecord.privateKey());
                wallet.setId(1L);
                walletRepository.save(wallet);
                return wallet;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Wallet loadWallet() {
        return walletRepository.findById(1L).orElseThrow();
    }

    public Transaction createTransaction( byte[] toAddress, Integer value, Long ledgerId) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        Wallet fromWallet = walletRepository.findById(1L).orElseThrow();
        Transaction result = new Transaction(
            getPublicKey(fromWallet).getEncoded(),
            toAddress,
            value ,
            LocalDateTime.now().toString(),
            null,
            ledgerId
            );

        String sr = result.toString();
        Signature signing = Signature.getInstance("SHA256withDSA");
        signing.initSign(getPrivateKey(fromWallet));
        signing.update(sr.getBytes());
        result.setSignature(signing.sign());
        return result;
    }
}
