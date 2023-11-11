package com.company.threads;

import com.company.service.BlockchainService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public class MiningThread extends Thread {
    private final BlockchainService blockchainService;

    public MiningThread(BlockchainService blockchainService) {
        super("MiningThread");
        this.blockchainService = blockchainService;
    }

    @Override
    public void run() {
        while (true) {
            long lastMinedBlock = LocalDateTime.parse(blockchainService.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
            if ((lastMinedBlock + BlockchainService.TIMEOUT_INTERVAL) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                    log.info("BlockChain is too old for mining! Update it from peers");
            } else if (((lastMinedBlock + BlockchainService.MINING_INTERVAL) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) > 0) {
                long miningInSec = ((lastMinedBlock + BlockchainService.MINING_INTERVAL) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
                //log.info("BlockChain is current, mining will commence in " + ((lastMinedBlock + BlockchainService.MINING_INTERVAL) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) + " seconds");
            } else {
                log.info("MINING NEW BLOCK");
                blockchainService.mineBlock();
                log.info(blockchainService.getWalletBallanceFX());
            }
//            log.info("{}", LocalDateTime.parse(blockchainService.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC));
            try {
                Thread.sleep(2000);
                if (blockchainService.isExit()) {
                    break;
                }
                blockchainService.setMiningPoints(blockchainService.getMiningPoints() + 2);
            } catch (InterruptedException e) {
                log.info("{}", e.getMessage(), e);
            }
        }
    }
}