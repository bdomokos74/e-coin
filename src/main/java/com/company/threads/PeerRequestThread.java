package com.company.threads;


import com.company.model.Block;
import com.company.servicedata.BlockchainData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;


public class PeerRequestThread extends Thread {

    private Socket socket;
    private final BlockchainData blockchainData;

    public PeerRequestThread(Socket socket, BlockchainData blockchainData) {
        this.socket = socket;
        this.blockchainData = blockchainData;
    }

    @Override
    public void run() {
        try {

            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());

            LinkedList<Block> recievedBC = (LinkedList<Block>) objectInput.readObject();
            System.out.println("LedgerId = " + recievedBC.getLast().getLedgerId()  +
                    " Size= " + recievedBC.getLast().getTransactionLedger().size());
           objectOutput.writeObject(blockchainData.getBlockchainConsensus(recievedBC));
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}