package com.company.controller;

import com.company.model.Transaction;
import com.company.service.WalletService;
import com.company.servicedata.BlockchainData;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.util.Base64;

@Component
public class AddNewTransactionController {
    @Autowired
    private BlockchainData blockchainData;
    @Autowired
    WalletService walletService;

    @FXML
    private TextField toAddress;
    @FXML
    private TextField value;

    @FXML
    public void createNewTransaction() throws GeneralSecurityException {
        Base64.Decoder decoder = Base64.getDecoder();
        Signature signing = Signature.getInstance("SHA256withDSA");
        byte[] sendB = decoder.decode(toAddress.getText());

        Integer ledgerId = blockchainData.getTransactionLedgerFX().get(0).getLedgerId();
        Transaction transaction = new Transaction(walletService.loadWallet(), sendB ,Integer.parseInt(value.getText()), ledgerId, signing);
        blockchainData.addTransaction(transaction,false);
        blockchainData.addTransactionState(transaction);
    }
}