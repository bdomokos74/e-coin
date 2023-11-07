package com.company.threads;


import com.company.service.BlockchainService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;


@Slf4j
public class PeerServer extends Thread {

    private final ServerSocket serverSocket;
    private final BlockchainService blockchainService;

    public PeerServer(Integer socketPort, BlockchainService blockchainService) throws IOException {
        this.serverSocket = new ServerSocket(socketPort);
        this.blockchainService = blockchainService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                new PeerRequestThread(serverSocket.accept(), blockchainService).start();
            } catch (IOException e) {
                log.info("{}", e.getMessage(), e);
            }
        }
    }
}