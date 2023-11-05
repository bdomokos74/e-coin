package com.company.threads;


import com.company.servicedata.BlockchainData;

import java.io.IOException;
import java.net.ServerSocket;


public class PeerServer extends Thread {

    private ServerSocket serverSocket;
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
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}