package com.company.service;

import com.company.model.Wallet;
import com.company.repository.WalletRepository;
import com.company.util.KeyPairRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

import static com.company.util.KeyHelper.createKeyPair;

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
}
