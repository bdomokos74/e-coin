package com.company;


import com.company.service.BlockchainService;
import com.company.threads.MiningThread;
import com.company.threads.PeerClient;
import com.company.threads.PeerServer;
import com.company.threads.UI;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class Main implements ApplicationListener<ECoin.StageReadyEvent> {
    private final BlockchainService blockchainService;
    private final ApplicationContext applicationContext;

    public static void main(String[] args) {
        Application.launch(ECoin.class, args);
    }

    @Override
    public void onApplicationEvent(ECoin.StageReadyEvent event) {
        log.info("onApplicationEvent");
        blockchainService.startBlockChain();

        Stage stage = event.getStage();
        List<Integer> peerPorts = Arrays.stream(System.getenv("peer.port").split(",")).map(Integer::parseInt).toList();
        int serverPort = Integer.parseInt(System.getenv("server.port"));
        new UI(System.getenv("name"), applicationContext).start(stage);
        new PeerClient(peerPorts, blockchainService).start();
        try {
            new PeerServer(serverPort, blockchainService).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new MiningThread(blockchainService).start();
    }

}
