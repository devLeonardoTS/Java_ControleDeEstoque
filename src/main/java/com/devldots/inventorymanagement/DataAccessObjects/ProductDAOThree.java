package com.devldots.inventorymanagement.DataAccessObjects;

import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.Constants.CategorySchema;
import com.devldots.inventorymanagement.Constants.ProductSchema;
import com.devldots.inventorymanagement.Interfaces.IDatabaseConnectionHandler;
import com.devldots.inventorymanagement.Interfaces.IImageHandler;
import com.devldots.inventorymanagement.Interfaces.IProductDataHandler;
import com.devldots.inventorymanagement.Models.Category;
import com.devldots.inventorymanagement.Models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOThree implements IProductDataHandler {

    private IDatabaseConnectionHandler databaseConnectionHandler;
    private List<String> errorList;

    public ProductDAOThree(IDatabaseConnectionHandler databaseConnectionHandler){
        this.databaseConnectionHandler = databaseConnectionHandler;
        this.errorList = new ArrayList<>();
    }

    @Override
    public boolean save(Product validatedProduct) {

        boolean hasImage = validatedProduct.getImageUid() != null && !validatedProduct.getImageUid().isBlank();

        Connection connection = this.databaseConnectionHandler.getConnection();

        String sql = null;
        if (hasImage){
            sql = "INSERT INTO " + ProductSchema.TABLE_ID +
                    " (" + ProductSchema.FK_CATEGORY +
                    ", " + ProductSchema.NAME +
                    ", " + ProductSchema.UNITARY_PRICE +
                    ", " + ProductSchema.QUANTITY +
                    ", " + ProductSchema.PHOTO_UID + ")" +
                    " VALUES" +
                    " (?, ?, ?, ?, ?);";
        } else {
            sql = "INSERT INTO " + ProductSchema.TABLE_ID +
                    " (" + ProductSchema.FK_CATEGORY +
                    ", " + ProductSchema.NAME +
                    ", " + ProductSchema.UNITARY_PRICE +
                    ", " + ProductSchema.QUANTITY + ")" +
                    " VALUES" +
                    " (?, ?, ?, ?);";
        }

        PreparedStatement pstmt = null;

        try {

            connection.setAutoCommit(false);

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, validatedProduct.getIdCategory());
            pstmt.setString(2, validatedProduct.getName());
            pstmt.setBigDecimal(3, validatedProduct.getUnitaryPrice());
            pstmt.setInt(4, validatedProduct.getQuantity());
            if (hasImage){ pstmt.setString(5, validatedProduct.getImageUid()); }

            int affectedRows = pstmt.executeUpdate();

            boolean isOperationSuccessful = affectedRows > 0;
            if (!isOperationSuccessful){
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException ex) {

            try {
                if (connection != null){

                    connection.rollback();

                }
            } catch (SQLException rollbackEx){
                System.getLogger(this.getClass().getName())
                        .log(System.Logger.Level.WARNING, ex.getMessage(), rollbackEx);
            }

            System.getLogger(this.getClass().getName())
                    .log(System.Logger.Level.WARNING, ex.getMessage(), ex);

            this.getErrorList().add("Failed to store product's data. Please contact the administrator with the following message: " + this.getClass().getSimpleName() + " - " + ex.getMessage());

            return false;

        } finally {

            try {
                if (connection != null) { connection.close(); }
                if (pstmt != null) { pstmt.close(); }
            } catch (SQLException ex){
                System.getLogger(this.getClass().getName())
                        .log(System.Logger.Level.ERROR, ex.getMessage(), ex);
            }

        }

    }

    @Override
    public Product get(Object id) throws NullPointerException, IllegalArgumentException {
        return null;
    }

    @Override
    public List<Product> getAll() {

        Connection connection = this.databaseConnectionHandler.getConnection();

        String sql = "SELECT " +
                ProductSchema.TABLE_ID + "." + ProductSchema.PK +
                ", " + ProductSchema.TABLE_ID + "." + ProductSchema.FK_CATEGORY +
                ", " + ProductSchema.TABLE_ID + "." + ProductSchema.NAME +
                ", " + ProductSchema.TABLE_ID + "." + ProductSchema.UNITARY_PRICE +
                ", " + ProductSchema.TABLE_ID + "." + ProductSchema.QUANTITY +
                ", " + ProductSchema.TABLE_ID + "." + ProductSchema.PHOTO_UID +
                ", " + ProductSchema.TABLE_ID + "." + ProductSchema.CREATED_AT +
                ", " + ProductSchema.TABLE_ID + "." + ProductSchema.UPDATED_AT +
                ", " + CategorySchema.TABLE_ID + "." + CategorySchema.PK +
                ", " + CategorySchema.TABLE_ID + "." + CategorySchema.NAME +
                " FROM " +
                ProductSchema.TABLE_ID + " AS " + ProductSchema.TABLE_ID +
                " INNER JOIN " + CategorySchema.TABLE_ID + " AS  " + CategorySchema.TABLE_ID +
                " ON " + CategorySchema.TABLE_ID + "." + CategorySchema.PK + " = " + ProductSchema.TABLE_ID + "." + ProductSchema.FK_CATEGORY + ";";

        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        List<Product> products = new ArrayList<>();

        try {

            pstmt = connection.prepareStatement(sql);
            resultSet = pstmt.executeQuery();

            boolean isProductListEmpty = !resultSet.isBeforeFirst();
            if (isProductListEmpty) { return products; }

            while (resultSet.next()){
                Product product = new Product();

                product.setIdProduct(resultSet.getInt(1));
                product.setIdCategory(resultSet.getInt(2));
                product.setName(resultSet.getString(3));
                product.setUnitaryPrice(resultSet.getBigDecimal(4));
                product.setQuantity(resultSet.getInt(5));
                product.setImageUid(resultSet.getString(6));
                product.setCreatedAt(LocalDateTime.parse(resultSet.getString(7)));
                product.setUpdatedAt(LocalDateTime.parse(resultSet.getString(8)));

                Category category = new Category();

                category.setIdCategory(resultSet.getInt(9));
                category.setName(resultSet.getString(10));

                product.setCategory(category);

                products.add(product);
            }

        } catch (SQLException | DateTimeParseException ex){

            System.getLogger(this.getClass().getName())
                    .log(System.Logger.Level.WARNING, ex.getMessage(), ex);

        } finally {

            try {
                if (connection != null) { connection.close(); }
                if (pstmt != null) { pstmt.close(); }
                if (resultSet != null) { resultSet.close(); }
            } catch (SQLException ex){
                System.getLogger(this.getClass().getName())
                        .log(System.Logger.Level.ERROR, ex.getMessage(), ex);
            }

        }

        return products;

    }

    @Override
    public boolean update(Product validatedProduct) {

        boolean hasImage = validatedProduct.getImageUid() != null && !validatedProduct.getImageUid().isBlank() && !validatedProduct.getImageUid().equals(AppConfig.DEFAULT_PRODUCT_IMG_FILE_NAME);

        Connection connection = this.databaseConnectionHandler.getConnection();

        String sql = null;
        if (hasImage){
            sql = "UPDATE " + ProductSchema.TABLE_ID +
                    " SET" +
                    " " + ProductSchema.FK_CATEGORY + " = ?" +
                    ", " + ProductSchema.NAME + " = ?" +
                    ", " + ProductSchema.UNITARY_PRICE + " = ?" +
                    ", " + ProductSchema.QUANTITY + " = ?" +
                    ", " + ProductSchema.PHOTO_UID + " = ?" +
                    ", " + ProductSchema.UPDATED_AT + " = strftime('%Y-%m-%dT%H:%M:%S', 'now', 'localtime')" +
                    " WHERE " +
                    ProductSchema.PK + " = ?;";
        } else {
            sql = "UPDATE " + ProductSchema.TABLE_ID +
                    " SET " +
                    ProductSchema.FK_CATEGORY + " = ?" +
                    ", " + ProductSchema.NAME + " = ?" +
                    ", " + ProductSchema.UNITARY_PRICE + " = ?" +
                    ", " + ProductSchema.QUANTITY + " = ?" +
                    ", " + ProductSchema.PHOTO_UID + " = " + "\"" + AppConfig.DEFAULT_PRODUCT_IMG_FILE_NAME + "\"" +
                    ", " + ProductSchema.UPDATED_AT + " = (strftime('%Y-%m-%dT%H:%M:%S', 'now', 'localtime'))" +
                    " WHERE " +
                    ProductSchema.PK + " = ?;";
        }

        PreparedStatement pstmt = null;

        try {

            connection.setAutoCommit(false);

            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, validatedProduct.getIdCategory());
            pstmt.setString(2, validatedProduct.getName());
            pstmt.setBigDecimal(3, validatedProduct.getUnitaryPrice());
            pstmt.setInt(4, validatedProduct.getQuantity());
            if (hasImage){
                pstmt.setString(5, validatedProduct.getImageUid());
                pstmt.setInt(6, validatedProduct.getIdProduct());
            } else {
                pstmt.setInt(5, validatedProduct.getIdProduct());
            }

            int affectedRows = pstmt.executeUpdate();
            boolean isOperationSuccessful = affectedRows > 0;

            if (!isOperationSuccessful){
                connection.rollback();
                this.getErrorList().add("Failed to update product's data. Please contact the administrator with the following message: "  + this.getClass().getSimpleName() + " - Couldn't find a selected product.");
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException ex) {

            try {
                if (connection != null){

                    connection.rollback();

                }
            } catch (SQLException rollbackEx){
                System.getLogger(this.getClass().getName())
                        .log(System.Logger.Level.WARNING, ex.getMessage(), rollbackEx);
            }

            System.getLogger(this.getClass().getName())
                    .log(System.Logger.Level.WARNING, ex.getMessage(), ex);

            this.getErrorList().add("Failed to update product's data. Please contact the administrator with the following message: " + this.getClass().getSimpleName() + " - " + ex.getMessage());

            return false;

        } finally {

            try {
                if (connection != null) { connection.close(); }
                if (pstmt != null) { pstmt.close(); }
            } catch (SQLException ex){
                System.getLogger(this.getClass().getName())
                        .log(System.Logger.Level.ERROR, ex.getMessage(), ex);
            }

        }

    }

    @Override
    public boolean delete(Object id) throws IllegalArgumentException {
        return false;
    }

    @Override
    public List<String> getErrorList() {
        return this.errorList;
    }

    @Override
    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }
}
