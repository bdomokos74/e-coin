package com.company.controller;

import com.company.model.Block;
import com.company.model.Transaction;
import com.company.service.BlockchainService;
import com.company.service.WalletService;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;

import static com.company.util.KeyHelper.getPublicKey;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainWindowController {

    private static final String CURRENT_BLOCK_TRANSACTION_LABEL = "Current block transactions";
    private static final String SPECIFIC_BLOCK_TRANSACTION_LABEL = "Transactions for block ledgerId=";
    public static final int REFRESH_SECONDS = 30;

    private final WalletService walletService;
    private final BlockchainService blockchainService;
    private final ApplicationContext applicationContext;

    @FXML
    public TableView<Transaction> tableview = new TableView<>(); //this is read-only UI table
    @FXML
    public TableView<Block> blockView = new TableView<>(); //this is read-only UI table

    @FXML
    public TableColumn transactionTableHeader;
    @FXML
    public Button resetTransactionsButton;

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
    @FXML
    private Button copyPkButton;

    @FXML
    private TabPane mainTabPane;

    // blockchain view
    @FXML
    private TableColumn<Transaction, String> ledgerId;
    @FXML
    private TableColumn<Transaction, String> prevHash;
    @FXML
    private TableColumn<Transaction, String> currHash;
    @FXML
    private TableColumn<Transaction, String> createdOn;
    @FXML
    private TableColumn<Transaction, String> createdBy;
    @FXML
    private TableColumn<Transaction, String> miningPoints;
    @FXML
    private TableColumn<Transaction, String> transactionNum;
    @FXML
    private TableColumn<Transaction, String> luck;

    private byte[] publicKeyBytes;
    private Long transactionsForBlockId = null;
    private String transactionTabLabel = CURRENT_BLOCK_TRANSACTION_LABEL;
    private final RefreshService refreshService = new RefreshService();

    public void initialize() {


        from.setCellValueFactory(new PropertyValueFactory<>("fromFX"));
        to.setCellValueFactory(new PropertyValueFactory<>("toFX"));
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
        signature.setCellValueFactory(new PropertyValueFactory<>("signatureFX"));
        timestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        ledgerId.setCellValueFactory(new PropertyValueFactory<>("ledgerId"));
        prevHash.setCellValueFactory(new PropertyValueFactory<>("prevHashFX"));
        currHash.setCellValueFactory(new PropertyValueFactory<>("currHashFX"));
        createdOn.setCellValueFactory(new PropertyValueFactory<>("timeStamp"));
        createdBy.setCellValueFactory(new PropertyValueFactory<>("createdByFX"));
        transactionNum.setCellValueFactory(new PropertyValueFactory<>("transactionNumFX"));
        miningPoints.setCellValueFactory(new PropertyValueFactory<>("miningPointsFX"));
        luck.setCellValueFactory(new PropertyValueFactory<>("luckFX"));

        addIconToButton(copyPkButton, "/icons/content_copy.png");
        addIconToButton(resetTransactionsButton, "/icons/refresh.png");

        eCoins.setText(blockchainService.getWalletBalance());

        setupPk();

        tableview.getSelectionModel().select(0);
        tableview.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                Transaction transaction = tableview.getSelectionModel().getSelectedItem();
                Base64.Encoder encoder = Base64.getEncoder();
                String value = encoder.encodeToString(transaction.getSignature());
                copyToClipboard(value);
            }
        });

        blockView.getSelectionModel().select(0);

        blockView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                log.info("selected: {}", blockView.getSelectionModel().getSelectedItem().toReadableString());
                Block block = blockView.getSelectionModel().getSelectedItem();
                transactionsForBlockId = block.getLedgerId();
                switchToTab(1);
                updateData();
            }
        });

        updateData();

        RefreshService refreshService = new RefreshService();
        refreshService.setPeriod(Duration.seconds(REFRESH_SECONDS));
        refreshService.setOnSucceeded(event -> {
            log.info("refreshing");
            updateData();
        });
        refreshService.start();
    }

    private void switchToTab(int i) {
        mainTabPane.getSelectionModel().select(i);
    }

    private void updateData() {
        log.info("Updating data, transactionsForBlockId={}", transactionsForBlockId);
        if (transactionsForBlockId == null) {
            tableview.setItems(blockchainService.getTransactionLedgerFX());
            transactionTabLabel = CURRENT_BLOCK_TRANSACTION_LABEL;
        } else {
            tableview.setItems(blockchainService.getTransactionsForBlockFX(transactionsForBlockId));
            transactionTabLabel = SPECIFIC_BLOCK_TRANSACTION_LABEL + " " + transactionsForBlockId;
        }
        transactionTableHeader.setText(transactionTabLabel);
        blockView.setItems(blockchainService.getBlockchainFX());
        eCoins.setText(blockchainService.getWalletBalance());
    }

    @FXML
    public void toNewTransactionController() {
        Dialog<ButtonType> newTransactionController = new Dialog<>();
        newTransactionController.initOwner(borderPane.getScene().getWindow());
        URL resource = getClass().getResource("/view/AddNewTransactionWindow.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        try {
            newTransactionController.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            log.info("Cant load dialog", e);
            return;
        }
        newTransactionController.getDialogPane().getButtonTypes().add(ButtonType.FINISH);
        Optional<ButtonType> result = newTransactionController.showAndWait();
        if (result.isPresent()) {
            tableview.setItems(blockchainService.getTransactionLedgerFX());
            eCoins.setText(blockchainService.getWalletBalance());
        }
    }

    @FXML
    public void refresh() {
        updateData();
    }

    @FXML
    public void onResetTransaction() {
        transactionsForBlockId = null;
        updateData();
    }

    @FXML
    public void handleExit() {
        blockchainService.setExit(true);
        Platform.exit();
    }

    private void setupPk() {
        publicKeyBytes = getPublicKey(walletService.loadWallet()).getEncoded();
        String pkHashString = DigestUtils.md5DigestAsHex(publicKeyBytes);
        publicKey.setText(pkHashString);
    }

    private void addIconToButton(Button button, String iconPath) {
        InputStream imgResource = getClass().getResourceAsStream(iconPath);
        Image copyImage = new Image(imgResource);
        ImageView img = new ImageView(copyImage);
        button.setGraphic(img);
    }

    @FXML
    public void copyPk() {
        Base64.Encoder encoder = Base64.getEncoder();
        String pk = encoder.encodeToString(publicKeyBytes);
        copyToClipboard(pk);
    }

    private static void copyToClipboard(String value) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(value);
        clipboard.setContent(content);
    }


    private static class RefreshService extends ScheduledService<Boolean> {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<>() {
                @Override
                protected Boolean call() {
                    return true;
                }
            };
        }
    }
}
