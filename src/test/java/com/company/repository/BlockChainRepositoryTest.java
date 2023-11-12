package com.company.repository;

import com.company.TestConfig;
import com.company.model.Block;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BlockChainRepositoryTest {
    @Autowired
    BlockChainRepository blockChainRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    void testSave() {
        Block block = new Block();
        block.setCurrHash("currHash".getBytes());
        block.setPrevHash("prevHash".getBytes());
        block.setLedgerId(1L);

        blockChainRepository.save(block);
        entityManager.flush();

        var resultList = entityManager.createQuery("select b from Block b", Block.class)
                .getResultList();
        assertThat(resultList.size()).isEqualTo(1);
    }
}