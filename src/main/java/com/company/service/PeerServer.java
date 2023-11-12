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
import java.net.SocketException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
@Component
public class PeerServer {
    private final Integer socketPort;
    private final BlockchainService blockchainService;
    private final AtomicReference<ServerSocket> serverSocketAtomicReference = new AtomicReference<>();

    @Autowired
    public PeerServer(@Value("${server.port}") Integer socketPort, BlockchainService blockchainService) {
        this.socketPort = socketPort;
        this.blockchainService = blockchainService;
    }

    public void stop() {
        try {
            serverSocketAtomicReference.get().close();
        } catch (IOException e) {
            log.info("exception while stopping", e);
        }
    }

    public void serve() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(socketPort)) {
                serverSocketAtomicReference.set(serverSocket);
                while (true) {
                    Socket socket = serverSocket.accept();
                    acceptRequest(socket);
                }
            } catch (SocketException se) {
                log.info("Server socket closing - {}", se.getMessage());
            } catch (IOException e) {
                log.info("Error in server thread: {}", e.getMessage(), e);
            }
        }, "ServerThread").start();
    }

    private void acceptRequest(Socket socket) {
        new Thread(() -> {
            try {
                serveRequest(socket);
            } catch (IOException | ClassNotFoundException e) {
                log.info("{}", e.getMessage(), e);
            }
        }, "RequestThread").start();
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