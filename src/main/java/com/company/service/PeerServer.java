package com.company.service;


import com.company.model.Block;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;


@Slf4j
@Component
public class PeerServer {
    private final Integer socketPort;
    private final BlockchainService blockchainService;
    @Autowired
    public PeerServer(@Value("${server.port}") Integer socketPort, BlockchainService blockchainService) {
        this.socketPort = socketPort;
        this.blockchainService = blockchainService;
    }

    public void serve() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(socketPort)) {
                while (true) {
                    acceptRequest(serverSocket);
                }
            } catch (IOException e) {
                log.info("Error in server thread: {}", e.getMessage(), e);
            }
        }, "ServerThread").start();
    }

    private void acceptRequest(ServerSocket serverSocket) {
        try {
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                try {
                    serveRequest(socket);
                } catch (IOException | ClassNotFoundException e) {
                    log.info("{}", e.getMessage(), e);
                }
            }, "RequestThread").start();
        } catch (IOException e) {
            log.info("{}", e.getMessage(), e);
        }
    }

    private void serveRequest(Socket socket) throws IOException, ClassNotFoundException {
        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
        LinkedList<Block> recievedBC = (LinkedList<Block>) objectInput.readObject();
        log.info("received: LedgerId = " + recievedBC.getLast().getLedgerId() + " Size= " + recievedBC.getLast().getTransactionLedger().size());
        log.debug("received lastblock: {}", recievedBC.getLast().toReadableString());
        LinkedList<Block> consensus = blockchainService.getBlockchainConsensus(recievedBC);
        log.debug("sending consensus, lastblock: {}", consensus.getLast().toReadableString());
        objectOutput.writeObject(consensus);
    }
}