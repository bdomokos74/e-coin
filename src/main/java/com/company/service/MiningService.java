package com.company.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
@RequiredArgsConstructor
public class MiningService {
    private final BlockchainService blockchainService;
    public void run() {

        long lastMinedBlock = LocalDateTime.parse(blockchainService.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
        if ((lastMinedBlock + BlockchainService.TIMEOUT_INTERVAL) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
            log.debug("BlockChain is too old for mining! Update it from peers");
        } else if (((lastMinedBlock + BlockchainService.MINING_INTERVAL) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) > 0) {
            long miningInSec = ((lastMinedBlock + BlockchainService.MINING_INTERVAL) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            log.debug("BlockChain is current, mining will commence in {} seconds", miningInSec);
        } else {
            log.info("MINING started");
            blockchainService.mineBlock();
            log.info(blockchainService.getWalletBalance());
        }
        blockchainService.setMiningPoints(blockchainService.getMiningPoints() + 2);

    }

}