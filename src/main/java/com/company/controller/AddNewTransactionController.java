package com.company.controller;

import com.company.model.Transaction;
import com.company.service.WalletService;
import com.company.service.BlockchainService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class AddNewTransactionController {
    private final BlockchainService blockchainService;
    private final WalletService walletService;

    @FXML
    private TextField toAddress;
    @FXML
    private TextField value;

    @FXML
    public void createNewTransaction() throws GeneralSecurityException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] sendB = decoder.decode(toAddress.getText());

        Long ledgerId = blockchainService.getTransactionLedgerFX().get(0).getLedgerId();
        Transaction transaction = walletService.createTransaction(sendB ,Integer.parseInt(value.getText()), ledgerId);
        blockchainService.addTransaction(transaction,false);
        blockchainService.addTransactionState(transaction);
    }
}
