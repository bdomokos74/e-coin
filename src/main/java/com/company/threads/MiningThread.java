package com.company.threads;

import com.company.servicedata.BlockchainData;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public class MiningThread extends Thread {
    private final BlockchainData blockchainData;

    public MiningThread(BlockchainData blockchainData) {
        this.blockchainData = blockchainData;
    }
    @Override
    public void run() {
        while (true) {
            long lastMinedBlock = LocalDateTime.parse(blockchainData
                    .getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC);
            if ((lastMinedBlock + BlockchainData.getTimeoutInterval()) < LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) {
                log.info("BlockChain is too old for mining! Update it from peers");
            } else if ( ((lastMinedBlock + BlockchainData.getMiningInterval()) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) > 0 ) {
                log.info("BlockChain is current, mining will commence in " +
                        ((lastMinedBlock + BlockchainData.getMiningInterval()) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) ) + " seconds");
            } else {
                log.info("MINING NEW BLOCK");
                    blockchainData.mineBlock();
                    log.info(blockchainData.getWalletBallanceFX());
            }
            log.info("{}", LocalDateTime.parse(blockchainData.getCurrentBlockChain().getLast().getTimeStamp()).toEpochSecond(ZoneOffset.UTC));
            try {
                Thread.sleep(2000);
                if (blockchainData.isExit()) { break; }
                blockchainData.setMiningPoints(blockchainData.getMiningPoints() + 2);
            } catch (InterruptedException e) {
                log.info("{}", e.getMessage(), e);
            }
        }
    }
}