package com.company.threads;


import com.company.servicedata.BlockchainData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;


@Slf4j
public class PeerServer extends Thread {

    private final ServerSocket serverSocket;
    private final BlockchainData blockchainData;

    public PeerServer(Integer socketPort, BlockchainData blockchainData) throws IOException {
        this.serverSocket = new ServerSocket(socketPort);
        this.blockchainData = blockchainData;
    }

    @Override
    public void run() {
        while (true) {
            try {
                new PeerRequestThread(serverSocket.accept(), blockchainData).start();
            } catch (IOException e) {
                log.info("{}", e.getMessage(), e);
            }
        }
    }
}