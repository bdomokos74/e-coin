package com.company;


import com.company.service.BlockchainService;
import com.company.service.PeerServer;
import jakarta.annotation.PreDestroy;
import javafx.application.Application;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.stream.Stream;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@EnableAsync
public class Main implements ApplicationListener<UI.StageReadyEvent> {
    private final BlockchainService blockchainService;
    private final PeerServer peerServer;

    public static void main(String[] args) {
        String name = System.getenv("name");
        Application.launch(UI.class, addNameToArgs(name, args));
    }

    @Override
    public void onApplicationEvent(UI.StageReadyEvent event) {
        blockchainService.startBlockChain();
        peerServer.serve();
    }

    @PreDestroy
    public void onExit() {
        peerServer.stop();
        log.info("Exiting...");
    }

    private static String[] addNameToArgs(String name, String[] args) {
        return Stream.concat(Stream.of(name), Arrays.stream(args)).toArray(String[]::new);
    }
}
