package com.company.threads;

import com.company.model.Block;
import com.company.servicedata.BlockchainData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class PeerClient extends Thread {

    private final Queue<Integer> queue = new ConcurrentLinkedQueue<>();
    private final BlockchainData blockchainData;

    public PeerClient(List<Integer> peerClientPorts, BlockchainData blockchainData) {
        this.blockchainData = blockchainData;
        this.queue.addAll(peerClientPorts);
    }

    @Override
    public void run() {
        while (true) {
            try (Socket socket = new Socket("127.0.0.1", queue.peek())) {
                log.info("Sending blockchain object on port: " + queue.peek());
                queue.add(queue.poll());
                socket.setSoTimeout(5000);

                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

                LinkedList<Block> blockChain = blockchainData.getCurrentBlockChain();
                objectOutput.writeObject(blockChain);

                LinkedList<Block> returnedBlockchain = (LinkedList<Block>) objectInput.readObject();
                log.info(" RETURNED BC LedgerId = " + returnedBlockchain.getLast().getLedgerId()  + " Size= " + returnedBlockchain.getLast().getTransactionLedger().size());
                blockchainData.getBlockchainConsensus(returnedBlockchain);
                Thread.sleep(2000);

            } catch (SocketTimeoutException e) {
                log.info("The socket timed out");
                queue.add(queue.poll());
            } catch (IOException e) {
                log.info("Client Error: " + e.getMessage() + " -- Error on port: "+ queue.peek());
                queue.add(queue.poll());
                try {
                    Thread.sleep(1000); // for now avoid spamming the log
                } catch (InterruptedException ex) {
                    log.info("{}", ex.getMessage(), ex);
                }
            } catch (InterruptedException | ClassNotFoundException e) {
                log.info("{}", e.getMessage(), e);
                queue.add(queue.poll());
            }
        }
    }
}
