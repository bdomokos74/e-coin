package com.company;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class ECoin extends Application {
    private ConfigurableApplicationContext applicationContext;
    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }
    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(Main.class).run();
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Object source) {
            super(source);
        }

        public Stage getStage() {
            return (Stage) getSource();
        }
    }
}

