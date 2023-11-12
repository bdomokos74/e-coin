package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;

@RequiredArgsConstructor
public class UI extends Application {
    private final String name;
    private final ApplicationContext applicationContext;

    @Override
    public void start(Stage stage) {
        Parent root;
        try {
            URL resource = getClass().getResource("/View/MainWindow.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            root = fxmlLoader.load();

            stage.setTitle("E-Coin - " + name);
            stage.setScene(new Scene(root, 900, 700));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}