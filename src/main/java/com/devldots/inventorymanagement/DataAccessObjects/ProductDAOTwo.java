package com.devldots.inventorymanagement.DataAccessObjects;

import com.devldots.inventorymanagement.Constants.ProductSchema;
import com.devldots.inventorymanagement.Interfaces.IDatabaseConnectionHandler;
import com.devldots.inventorymanagement.Interfaces.IImageHandler;
import com.devldots.inventorymanagement.Interfaces.IProductDataHandler;
import com.devldots.inventorymanagement.Models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOTwo {

    private IDatabaseConnectionHandler databaseConnectionHandler;
    private IImageHandler productImageHandler;
    private List<String> errorList;

    public ProductDAOTwo(IDatabaseConnectionHandler databaseConnectionHandler) {
        this.databaseConnectionHandler = databaseConnectionHandler;
        this.errorList = new ArrayList<>();
    }

    //@Override
//    public boolean save(Product validatedProduct, String imagePath, IImageHandler imageHandler) {
//
//
//        boolean hasImage = validatedProduct.getImageUid() != null && !validatedProduct.getImageUid().isBlank();
//
//        Connection connection = this.databaseConnectionHandler.getConnection();
//
//        String sql = null;
//        if (hasImage){
//            sql = "INSERT INTO " + ProductSchema.TABLE_ID +
//                    " (" + ProductSchema.FK_CATEGORY +
//                    ", " + ProductSchema.NAME +
//                    ", " + ProductSchema.UNITARY_PRICE +
//                    ", " + ProductSchema.QUANTITY +
//                    ", " + ProductSchema.PHOTO_UID + ")" +
//                    " VALUES" +
//                    " (?, ?, ?, ?, ?);";
//        } else {
//            sql = "INSERT INTO " + ProductSchema.TABLE_ID +
//                    " (" + ProductSchema.FK_CATEGORY +
//                    ", " + ProductSchema.NAME +
//                    ", " + ProductSchema.UNITARY_PRICE +
//                    ", " + ProductSchema.QUANTITY + ")" +
//                    " VALUES" +
//                    " (?, ?, ?, ?);";
//        }
//
//        PreparedStatement pstmt = null;
//
//        try {
//
//            connection.setAutoCommit(false);
//
//            pstmt = connection.prepareStatement(sql);
//            pstmt.setInt(1, validatedProduct.getIdCategory());
//            pstmt.setString(2, validatedProduct.getName());
//            pstmt.setBigDecimal(3, validatedProduct.getUnitaryPrice());
//            pstmt.setInt(4, validatedProduct.getQuantity());
//            if (hasImage){ pstmt.setString(5, validatedProduct.getImageUid()); }
//
//            int affectedRows = pstmt.executeUpdate();
//
//            boolean isOperationSuccessful = affectedRows > 0;
//            if (!isOperationSuccessful){
//                connection.rollback();
//                return false;
//            }
//
//            if (hasImage){
//                boolean isImageStored = this.getProductImageHandler().saveImageToDisk(imagePath, 150);
//                if (!isImageStored){
//                    connection.rollback();
//                    if (!this.getProductImageHandler().getErrorList().isEmpty()){
//                        this.getErrorList().addAll(this.getProductImageHandler().getErrorList());
//                        return false;
//                    }
//                }
//            }
//
//            connection.commit();
//            return true;
//
//        } catch (SQLException ex) {
//
//            try {
//                if (connection != null){
//
//                    boolean isProductImageHandled = hasImage && this.getProductImageHandler().getImageId() != null;
//                    if (isProductImageHandled){
//                        this.getProductImageHandler().removeImageFromDisk(this.getProductImageHandler().getImageId());
//
//                        if (!this.getProductImageHandler().getErrorList().isEmpty()){
//                            this.getErrorList().addAll(this.getProductImageHandler().getErrorList());
//                        }
//                    }
//
//                    connection.rollback();
//
//                }
//            } catch (SQLException rollbackEx){
//                System.getLogger(this.getClass().getName())
//                        .log(System.Logger.Level.WARNING, ex.getMessage(), rollbackEx);
//            }
//
//            System.getLogger(this.getClass().getName())
//                    .log(System.Logger.Level.WARNING, ex.getMessage(), ex);
//
//            this.getErrorList().add("Failed to store product's data. Please contact the administrator with the following message: " + this.getClass().getSimpleName() + " - " + ex.getMessage());
//
//            return false;
//
//        } finally {
//
//            try {
//                if (connection != null) { connection.close(); }
//                if (pstmt != null) { pstmt.close(); }
//            } catch (SQLException ex){
//                System.getLogger(this.getClass().getName())
//                        .log(System.Logger.Level.ERROR, ex.getMessage(), ex);
//            }
//
//        }
//
//    }

    //@Override
    public Product get(Object id) throws NullPointerException, IllegalArgumentException {
        return null;
    }

    //@Override
    public List<Product> getAll() {

        Connection connection = this.databaseConnectionHandler.getConnection();

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

    //@Override
    public boolean update(Product validatedProduct, String imagePath, IImageHandler imageHandler) {

        // boolean isUnchangedDefaultImage = imagePath != null && imagePath.isBlank() && validatedProduct.getImageUid().equals(AppConfig.DEFAULT_PRODUCT_IMG_FILE_NAME);
        // boolean isUnchangedCustomImage = imagePath != null && imagePath.contains(validatedProduct.getImageUid());
        // boolean hasNewImage = !(isUnchangedDefaultImage || isUnchangedCustomImage);

        return false;
    }

    //@Override
    public boolean delete(Object id) throws IllegalArgumentException {
        return false;
    }

    //@Override
    public List<String> getErrorList() {
        return null;
    }

    //@Override
    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    public IDatabaseConnectionHandler getDatabaseConnectionHandler() {
        return databaseConnectionHandler;
    }

    public void setDatabaseConnectionHandler(IDatabaseConnectionHandler databaseConnectionHandler) {
        this.databaseConnectionHandler = databaseConnectionHandler;
    }

    public IImageHandler getProductImageHandler() {
        return productImageHandler;
    }

    public void setProductImageHandler(IImageHandler productImageHandler) {
        this.productImageHandler = productImageHandler;
    }

}
