package com.company.controller;

import com.company.model.Transaction;
import com.company.service.WalletService;
import com.company.servicedata.BlockchainData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static com.company.util.KeyHelper.getPublicKey;

@Controller
@Slf4j
public class MainWindowController {
    @Autowired
    private WalletService walletService;
    @Autowired
    private BlockchainData blockchainData;
    
    @FXML
    public TableView<Transaction> tableview = new TableView<>(); //this is read-only UI table
    @FXML
    private TableColumn<Transaction, String> from;
    @FXML
    private TableColumn<Transaction, String> to;
    @FXML
    private TableColumn<Transaction, Integer> value;
    @FXML
    private TableColumn<Transaction, String> timestamp;
    @FXML
    private TableColumn<Transaction, String> signature;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField eCoins;
    @FXML
    private TextArea publicKey;

    public void initialize() {
        Base64.Encoder encoder = Base64.getEncoder();
        from.setCellValueFactory(
                new PropertyValueFactory<>("fromFX"));
        to.setCellValueFactory(
                new PropertyValueFactory<>("toFX"));
        value.setCellValueFactory(
                new PropertyValueFactory<>("value"));
        signature.setCellValueFactory(
                new PropertyValueFactory<>("signatureFX"));
        timestamp.setCellValueFactory(
                new PropertyValueFactory<>("timestamp"));
        eCoins.setText(blockchainData.getWalletBallanceFX());
        publicKey.setText(encoder.encodeToString(getPublicKey(walletService.loadWallet()).getEncoded()));
        tableview.setItems(blockchainData.getTransactionLedgerFX());
        tableview.getSelectionModel().select(0);
    }

    @FXML
    public void toNewTransactionController() {
        Dialog<ButtonType> newTransactionController = new Dialog<>();
        newTransactionController.initOwner(borderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/View/AddNewTransactionWindow.fxml"));
        try {
            newTransactionController.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            log.info("Cant load dialog", e);
            return;
        }
        newTransactionController.getDialogPane().getButtonTypes().add(ButtonType.FINISH);
        Optional<ButtonType> result = newTransactionController.showAndWait();
        if (result.isPresent() ) {
            tableview.setItems(blockchainData.getTransactionLedgerFX());
            eCoins.setText(blockchainData.getWalletBallanceFX());
        }
    }

    @FXML
    public void refresh() {
        tableview.setItems(blockchainData.getTransactionLedgerFX());
        tableview.getSelectionModel().select(0);
        eCoins.setText(blockchainData.getWalletBallanceFX());
    }

    @FXML
    public void handleExit() {
        blockchainData.setExit(true);
        Platform.exit();
    }
}
