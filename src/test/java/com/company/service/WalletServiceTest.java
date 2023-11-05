package com.company.service;

import com.company.TestConfig;
import com.company.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static com.company.util.KeyHelper.getPrivateKey;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WalletServiceTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private WalletService walletService;

    @Test
    void name() {
        Wallet w = walletService.getOrCreateWallet();
        entityManager.flush();

        Wallet w2 = walletService.loadWallet();
        getPrivateKey(w2.getPrivateKey());
    }
}