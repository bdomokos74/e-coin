package com.company.repository;

import com.company.model.Block;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.LinkedList;
import java.util.List;

public interface BlockChainRepository extends JpaRepository<Block, Long> {
//    @EntityGraph(attributePaths = {"transaction"})
//    List<Block> findAll();
}
