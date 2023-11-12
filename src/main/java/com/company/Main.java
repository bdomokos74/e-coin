package com.company;


import com.company.service.BlockchainService;
import com.company.threads.PeerClient;
import com.company.threads.PeerServer;
import com.company.threads.UI;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@EnableAsync
public class Main implements ApplicationListener<ECoin.StageReadyEvent> {
    private final BlockchainService blockchainService;
    private final ApplicationContext applicationContext;
    private final PeerServer peerServer;
    private final PeerClient peerClient;

    public static void main(String[] args) {
        Application.launch(ECoin.class, args);
    }

    @Value("${name}")
    private String name;

    @Override
    public void onApplicationEvent(ECoin.StageReadyEvent event) {
        log.info("onApplicationEvent: {}", name);
        blockchainService.startBlockChain();

        Stage stage = event.getStage();
        new UI(name, applicationContext).start(stage);

        peerServer.serve();
    }

}
