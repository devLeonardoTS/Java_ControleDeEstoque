package com.devldots.controleestoque;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Parent root = null;

        try {
            root = FXMLLoader.load(getClass().getResource("estoque.fxml"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
