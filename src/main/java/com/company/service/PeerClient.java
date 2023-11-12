package com.company.service;

import com.company.model.Block;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class PeerClient {
    public static final int SOCKET_TIMEOUT_MILLIS = 5000;
    private final Queue<Integer> queue = new ConcurrentLinkedQueue<>();
    private final BlockchainService blockchainService;

    @Value("${peer.port}")
    private String clientPorts;

    @PostConstruct
    void init() {
        log.info("adding ports: {}", clientPorts);
        this.queue.addAll(getPeerPorts(clientPorts));
    }

    private static List<Integer> getPeerPorts(String ports) {
        return Arrays.stream(ports.split(",")).map(Integer::parseInt).toList();
    }

    public void sendPeerRequest() {
        try (Socket socket = new Socket("127.0.0.1", queue.peek())) {
            log.info("Sending blockchain object on port: " + queue.peek());
            queue.add(queue.poll());
            socket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

            LinkedList<Block> blockChain = blockchainService.getCurrentBlockChain();
            log.info("sending: ledgerId={}, ts={}", blockChain.getLast().getLedgerId(), blockChain.getLast().getTimeStamp());
            objectOutput.writeObject(blockChain);

            LinkedList<Block> returnedBlockchain = (LinkedList<Block>) objectInput.readObject();
            if(!returnedBlockchain.isEmpty()) {
                log.info("\tRETURNED BC LedgerId = " + returnedBlockchain.getLast().getLedgerId() + " Size= " + returnedBlockchain.getLast().getTransactionLedger().size());
            }
            LinkedList<Block> consensus = blockchainService.getBlockchainConsensus(returnedBlockchain);
            log.debug("\tconsensus: {}", consensus.getLast());

//            Thread.sleep(CLIENT_SLEEP_MILLIS);

        } catch (SocketTimeoutException e) {
            log.info("The socket timed out");
            queue.add(queue.poll());
        } catch (IOException e) {
//                log.info("Client Error: " + e.getMessage() + " -- Error on port: "+ queue.peek());
            queue.add(queue.poll());
            try {
                Thread.sleep(1000); // for now avoid spamming the log
            } catch (InterruptedException ex) {
                log.info("{}", ex.getMessage(), ex);
            }
        } catch ( ClassNotFoundException e) {
            log.info("{}", e.getMessage(), e);
            queue.add(queue.poll());
        }
    }
}

