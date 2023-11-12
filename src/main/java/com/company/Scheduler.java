package com.company;

import com.company.service.MiningService;
import com.company.service.PeerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final PeerClient peerClient;
    private final MiningService miningService;
    public static final int CLIENT_SLEEP_MILLIS = 4000;
    public static final int MINING_DELAY_MILLIS = 2000;

    @Scheduled(fixedDelay = CLIENT_SLEEP_MILLIS, initialDelay = CLIENT_SLEEP_MILLIS)
    public void peerRequestTask() {
        peerClient.sendPeerRequest();
    }

    @Scheduled(fixedDelay = MINING_DELAY_MILLIS, initialDelay = MINING_DELAY_MILLIS)
    public void miningTask() {
        miningService.run();
    }

}
