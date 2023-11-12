package com.company.repository;

import com.company.model.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockChainRepository extends JpaRepository<Block, Long> {
}
