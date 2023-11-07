package com.company.threads;


import com.company.model.Block;
import com.company.service.BlockchainService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

@Slf4j
public class PeerRequestThread extends Thread {

    private final Socket socket;
    private final BlockchainService blockchainService;

    public PeerRequestThread(Socket socket, BlockchainService blockchainService) {
        this.socket = socket;
        this.blockchainService = blockchainService;
    }

    @Override
    public void run() {
        try {

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

            LinkedList<Block> recievedBC = (LinkedList<Block>) objectInput.readObject();
            log.info("LedgerId = " + recievedBC.getLast().getLedgerId()  +
                    " Size= " + recievedBC.getLast().getTransactionLedger().size());
           objectOutput.writeObject(blockchainService.getBlockchainConsensus(recievedBC));
        } catch (IOException | ClassNotFoundException e) {
            log.info("{}", e.getMessage(), e);
        }
    }
}