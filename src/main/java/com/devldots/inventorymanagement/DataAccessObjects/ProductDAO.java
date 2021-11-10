package com.devldots.inventorymanagement.DataAccessObjects;

import com.devldots.inventorymanagement.Constants.ProductSchema;
import com.devldots.inventorymanagement.Interfaces.IDataAccessObject;
import com.devldots.inventorymanagement.Interfaces.IDatabaseConnectionHandler;
import com.devldots.inventorymanagement.Interfaces.IImageHandler;
import com.devldots.inventorymanagement.Models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements IDataAccessObject<Product> {

    private IDatabaseConnectionHandler dbConnectable;
    private IImageHandler productImageHandler;
    private List<String> errorList;

    public ProductDAO(IDatabaseConnectionHandler dbConnectable){
        this.dbConnectable = dbConnectable;
        this.errorList = new ArrayList<>();
    }

    public ProductDAO(IDatabaseConnectionHandler dbConnectable, IImageHandler productImageHandler){
        this.dbConnectable = dbConnectable;
        this.productImageHandler = productImageHandler;
        this.errorList = new ArrayList<>();
    }

    @Override
    public boolean save(Product validatedProduct) {

        if (this.productImageHandler == null){ return false; }

        Connection connection = this.dbConnectable.getConnection();

        String sql = "";
        boolean hasCustomImage = validatedProduct.getImageUid() != null && !validatedProduct.getImageUid().isBlank();
        if (hasCustomImage){
            sql = "INSERT INTO " + ProductSchema.TABLE_ID +
                    " (" + ProductSchema.FK_CATEGORY + ", " + ProductSchema.NAME + ", " + ProductSchema.UNITARY_PRICE + ", " + ProductSchema.QUANTITY + ", " + ProductSchema.PHOTO_UID + ")" +
                    " VALUES" +
                    " (?, ?, ?, ?, ?);";
        } else {
            sql = "INSERT INTO " + ProductSchema.TABLE_ID +
                    " (" + ProductSchema.FK_CATEGORY + ", " + ProductSchema.NAME + ", " + ProductSchema.UNITARY_PRICE + ", " + ProductSchema.QUANTITY + ")" +
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
            if (hasCustomImage){ pstmt.setString(5, validatedProduct.getImageUid()); }

            int affectedRows = pstmt.executeUpdate();
            boolean isOperationSuccessful = affectedRows > 0;

            if (!isOperationSuccessful){
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException ex){

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

        Connection connection = this.dbConnectable.getConnection();

        String sql = "SELECT " +
                ProductSchema.PK + ", " + ProductSchema.FK_CATEGORY + ", " + ProductSchema.NAME +
                ", " + ProductSchema.UNITARY_PRICE + ", " + ProductSchema.QUANTITY + ", " + ProductSchema.PHOTO_UID +
                ", " + ProductSchema.CREATED_AT + ", " + ProductSchema.UPDATED_AT +
                " FROM " +
                ProductSchema.TABLE_ID +
                " GROUP BY " + ProductSchema.PK + ";";

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

                product.setIdProduct(resultSet.getInt(ProductSchema.PK));
                product.setIdCategory(resultSet.getInt(ProductSchema.FK_CATEGORY));
                product.setName(resultSet.getString(ProductSchema.NAME));
                product.setUnitaryPrice(resultSet.getBigDecimal(ProductSchema.UNITARY_PRICE));
                product.setQuantity(resultSet.getInt(ProductSchema.QUANTITY));
                product.setImageUid(resultSet.getString(ProductSchema.PHOTO_UID));
                product.setCreatedAt(LocalDateTime.parse(resultSet.getString(ProductSchema.CREATED_AT)));
                product.setUpdatedAt(LocalDateTime.parse(resultSet.getString(ProductSchema.UPDATED_AT)));

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

        if (this.productImageHandler == null){ return false; }

        Connection connection = this.dbConnectable.getConnection();

        String sql = "";
        boolean hasCustomImage = validatedProduct.getImageUid() != null && !validatedProduct.getImageUid().isBlank();
        System.out.println("hasCustomImage? " + (hasCustomImage));
        if (hasCustomImage){
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
                    ", " + ProductSchema.PHOTO_UID + " = (default)" +
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
            if (hasCustomImage){
                pstmt.setString(5, validatedProduct.getImageUid());
                pstmt.setInt(6, validatedProduct.getIdProduct());
            } else {
                pstmt.setInt(5, validatedProduct.getIdProduct());
            }

            int affectedRows = pstmt.executeUpdate();
            boolean isOperationSuccessful = affectedRows > 0;

            if (!isOperationSuccessful){
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException ex){

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
        return null;
    }

    @Override
    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    public IDatabaseConnectionHandler getDbConnectable() {
        return dbConnectable;
    }

    public void setDbConnectable(IDatabaseConnectionHandler dbConnectable) {
        this.dbConnectable = dbConnectable;
    }

    public IImageHandler getProductImageHandler() {
        return productImageHandler;
    }

    public void setProductImageHandler(IImageHandler productImageHandler) {
        this.productImageHandler = productImageHandler;
    }
}
