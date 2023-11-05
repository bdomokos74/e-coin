package com.company.repository;

import com.company.model.BlockChainRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockChainRepository extends JpaRepository<BlockChainRecord, Long> {
}
