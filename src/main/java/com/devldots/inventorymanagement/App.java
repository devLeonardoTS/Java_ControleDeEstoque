package com.devldots.inventorymanagement;

import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.Interfaces.IDatabaseConnectionHandler;
import com.devldots.inventorymanagement.Factory.SQLiteConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class App extends Application {

    public static void main(String[] args) { launch(); }

    @Override
    public void start(Stage stage) throws IOException {

        AppConfig.createFileDirectories();
        createDbConnection(new SQLiteConnection());
        // checkDbProductsTable();

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("inventory-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Controle de Estoque Simples");
        stage.setScene(scene);
        stage.show();

    }

    private void createDbConnection(IDatabaseConnectionHandler dbConnectable){

        Connection connection = dbConnectable.getConnection();

        try {
            connection.close();
        } catch (SQLException sqlEx){
            System.getLogger(App.class.getName())
                .log(System.Logger.Level.WARNING, sqlEx.getMessage(), sqlEx);
        }

    }

    private void checkDbProductsTable(){

        Connection conn = null;

        try {
            IDatabaseConnectionHandler dbConnection = new SQLiteConnection();

            conn = dbConnection.getConnection();

            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT count(*) FROM products;");
            rset.next();
            System.out.println("Products Table has "+ rset.getInt("count(*)") + " rows.");
        }
        catch (Exception ex){
            System.getLogger(App.class.getName())
                .log(System.Logger.Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            try {
                if (conn != null){ conn.close(); }
            }
            catch (SQLException ex){
                System.getLogger(App.class.getName())
                    .log(System.Logger.Level.WARNING, ex.getMessage(), ex);
            }
        }

    }

}