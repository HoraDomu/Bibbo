package com.bibbo;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new BibboApp(stage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
