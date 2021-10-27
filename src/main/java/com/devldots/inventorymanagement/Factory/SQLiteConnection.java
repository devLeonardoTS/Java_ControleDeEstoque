package com.devldots.inventorymanagement.Factory;

import com.devldots.inventorymanagement.App;
import com.devldots.inventorymanagement.Configs.AppConfig;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.lang.System.Logger;
import java.sql.*;

public class SQLiteConnection implements IDbConnection {

    @Override
    public Connection getConnection() {

        String connectionString = "jdbc:sqlite:" + AppConfig.DEFAULT_DB_PATH;

        boolean dbExists = new File(AppConfig.DEFAULT_DB_PATH).exists();

        Connection connection = null;

        try
        {
            connection = DriverManager.getConnection(connectionString);

            if (connection != null){

                System.out.println("A connection to the database has been established.");

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
                    System.getLogger(SQLiteConnection.class.getName())
                        .log(System.Logger.Level.ERROR, sqlCloseEx.getMessage(), sqlCloseEx);
                }
            }

            System.getLogger(SQLiteConnection.class.getName())
                .log(System.Logger.Level.ERROR, sqlEx.getMessage(), sqlEx);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Attention!");
            alert.setHeaderText("Attention!");
            alert.setContentText("Couldn't access the database, closing the application.");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.OK);
            alert.showAndWait();

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
            insertTestProducts(connection);

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
        String sql = "CREATE TABLE IF NOT EXISTS categories (\n" +
            " id_category INTEGER PRIMARY KEY NOT NULL CHECK(id_category > 0),\n" +
            " name TEXT NOT NULL UNIQUE\n" +
            " );";

        Statement stm = connection.createStatement();
        stm.executeUpdate(sql);
    }

    private void insertDefaultCategories(Connection connection) throws SQLException {
        String sql = "INSERT INTO categories \n" +
            " (name) \n" +
            " VALUES" +
            " (\"Eletrônicos\"),\n" +
            " (\"Ferramentas\"),\n" +
            " (\"Brinquedos\");";

        Statement stm = connection.createStatement();
        stm.execute(sql);
        System.out.println("Inserted values into categories table");
    }

    private void insertTestProducts(Connection connection) throws SQLException {
        String sql = "INSERT INTO products \n" +
                " (id_category, name, unitary_price, quantity) \n" +
                " VALUES" +
                " (1, \"Mouse Logitech M90\", 25.50, 10);";

        Statement stm = connection.createStatement();
        stm.execute(sql);
        System.out.println("Inserted values into products table");
    }

    private void createProductsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS products (\n" +
            " id_product INTEGER PRIMARY KEY NOT NULL CHECK(id_product > 0),\n" +
            " id_category INTEGER NOT NULL CHECK(id_category > 0),\n" +
            " name TEXT NOT NULL,\n" +
            " unitary_price DECIMAL(9,2) NOT NULL CHECK(unitary_price > 0),\n" +
            " quantity INTEGER NOT NULL CHECK(quantity > 0),\n" +
            " photo_uid TEXT NOT NULL DEFAULT 'default_product_img.png',\n" +
            " created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),\n" +
            " updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),\n" +
            " FOREIGN KEY (id_category) REFERENCES categories (id_category)\n" +
            ");";

        Statement stm = connection.createStatement();
        stm.execute(sql);
    }

}
