package com.devldots.inventorymanagement.Factory;

import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.Constants.CategorySchema;
import com.devldots.inventorymanagement.Constants.ProductSchema;
import com.devldots.inventorymanagement.Interfaces.IDatabaseConnectionHandler;
import com.devldots.inventorymanagement.Utils.AppLogger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;

public class SQLiteConnection implements IDatabaseConnectionHandler {

    @Override
    public Connection getConnection() {

        String connectionString = "jdbc:sqlite:" + AppConfig.DEFAULT_DB_PATH;

        boolean dbExists = new File(AppConfig.DEFAULT_DB_PATH).exists();

        Connection connection = null;

        try
        {
            connection = DriverManager.getConnection(connectionString);

            if (connection != null){

                // System.out.println("A connection to the database has been established.");

                if (!dbExists){
                    createDbTables(connection);
                }

            }
        }
        catch (SQLException sqlEx)
        {
            if (connection != null) {
                try {
                    connection.close();
                    connection = null;
                }
                catch (SQLException sqlCloseEx)
                {
                    AppLogger.getAppLogger(this.getClass().getName())
                        .log(Level.SEVERE, sqlCloseEx.getMessage(), sqlCloseEx);
                }
            }

            AppLogger.getAppLogger(this.getClass().getName())
                    .log(Level.SEVERE, sqlEx.getMessage(), sqlEx);

            if (Platform.isFxApplicationThread()) {
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Attention!");
                    alert.setHeaderText("Attention!");
                    alert.setContentText("Couldn't access the database, closing the application. Please send the latest log file to the administrator.");
                    alert.getButtonTypes().clear();
                    alert.getButtonTypes().add(ButtonType.OK);
                    alert.showAndWait();

                });
            }

            System.exit(0);

        }

        return connection;

    }

    private void createDbTables(Connection connection) throws SQLException {

        try {
            connection.setAutoCommit(false);

            createCategoriesTable(connection);
            createProductsTable(connection);
            insertDefaultCategories(connection);
//            insertTestProducts(connection);

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException sqlEx){
            connection.rollback();
            connection.close();

            new File(AppConfig.DEFAULT_DB_PATH).delete();

            throw sqlEx;
        }

    }

    private void createCategoriesTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + CategorySchema.TABLE_ID + " (\n" +
            " " + CategorySchema.PK + " INTEGER PRIMARY KEY NOT NULL CHECK(" + CategorySchema.PK + " > 0),\n" +
            " " + CategorySchema.NAME + " TEXT NOT NULL UNIQUE\n" +
            " );";

        Statement stm = connection.createStatement();
        stm.executeUpdate(sql);
    }

    private void createProductsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + ProductSchema.TABLE_ID + " (\n" +
            " " + ProductSchema.PK + " INTEGER PRIMARY KEY NOT NULL CHECK(" + ProductSchema.PK + " > 0),\n" +
            " " + ProductSchema.FK_CATEGORY + " INTEGER NOT NULL CHECK(" + ProductSchema.FK_CATEGORY + " > 0),\n" +
            " " + ProductSchema.NAME + " TEXT NOT NULL,\n" +
            " " + ProductSchema.UNITARY_PRICE + " DECIMAL(9,2) NOT NULL CHECK(" + ProductSchema.UNITARY_PRICE + " > 0),\n" +
            " " + ProductSchema.QUANTITY + " INTEGER NOT NULL CHECK(" + ProductSchema.QUANTITY + " > 0),\n" +
            " " + ProductSchema.PHOTO_UID + " TEXT NOT NULL DEFAULT '" + AppConfig.DEFAULT_PRODUCT_IMG_FILE_NAME + "',\n" +
            " " + ProductSchema.CREATED_AT + " TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%S', 'now', 'localtime')),\n" +
            " " + ProductSchema.UPDATED_AT + " TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%S', 'now', 'localtime')),\n" +
            " FOREIGN KEY (" + ProductSchema.FK_CATEGORY + ") REFERENCES " + CategorySchema.TABLE_ID + " (" + CategorySchema.PK + ")\n" +
            ");";

        Statement stm = connection.createStatement();
        stm.execute(sql);
    }

    private void insertDefaultCategories(Connection connection) throws SQLException {
        String sql = "INSERT INTO " + CategorySchema.TABLE_ID + " \n" +
                " (name) \n" +
                " VALUES" +
                " (\"Eletrônicos\"),\n" +
                " (\"Ferramentas\"),\n" +
                " (\"Brinquedos\");";

        Statement stm = connection.createStatement();
        stm.execute(sql);
        System.out.println("Inserted values into categories table");

        // Todo: When in final stage, remove "insertDefaultCategories()";
    }

//    private void insertTestProducts(Connection connection) throws SQLException {
//        String sql = "INSERT INTO " + ProductSchema.TABLE_ID + " \n" +
//                " (id_category, name, unitary_price, quantity) \n" +
//                " VALUES" +
//                " (1, \"Mouse Logitech M90\", 25.50, 10);";
//
//        Statement stm = connection.createStatement();
//        stm.execute(sql);
//        System.out.println("Inserted values into products table");
//
//        // Todo: When in final stage, remove "insertTestProducts()";
//    }

}
