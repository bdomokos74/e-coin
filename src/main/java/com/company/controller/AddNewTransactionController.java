package com.company.controller;

import com.company.model.Transaction;
import com.company.servicedata.BlockchainData;
import com.company.servicedata.WalletData;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.security.GeneralSecurityException;
import java.security.Signature;
import java.util.Base64;

public class AddNewTransactionController {

    @FXML
    private TextField toAddress;
    @FXML
    private TextField value;

    @FXML
    public void createNewTransaction() throws GeneralSecurityException {
        Base64.Decoder decoder = Base64.getDecoder();
        Signature signing = Signature.getInstance("SHA256withDSA");
        byte[] sendB = decoder.decode(toAddress.getText());

        Integer ledgerId = BlockchainData.getInstance().getTransactionLedgerFX().get(0).getLedgerId();
        Transaction transaction = new Transaction(WalletData.getInstance().getWallet(), sendB ,Integer.parseInt(value.getText()), ledgerId, signing);
        BlockchainData.getInstance().addTransaction(transaction,false);
        BlockchainData.getInstance().addTransactionState(transaction);
    }
}