package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.Constants.ProductSchema;
import com.devldots.inventorymanagement.Factory.IDbConnection;
import com.devldots.inventorymanagement.Interfaces.IInventoryManipulationCallbacks;
import com.devldots.inventorymanagement.Models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class GetProductsService {

    private IDbConnection dbConnectable;
    private IInventoryManipulationCallbacks inventoryManipulationCallbacks;

    private List<Product> products;

    public GetProductsService(IDbConnection dbConnectable, IInventoryManipulationCallbacks inventoryManipulationCallbacks){
        this.dbConnectable = dbConnectable;
        this.inventoryManipulationCallbacks = inventoryManipulationCallbacks;
    }

    public void execute(){
        Thread thread = new Thread(() -> {
            inventoryManipulationCallbacks.handleProductList(this.fetchProducts());
        });
        thread.start();
    }

    public List<Product> fetchProducts(){

        Connection connection = this.dbConnectable.getConnection();
        this.setProducts(new ArrayList());

        String sql = "SELECT " +
                ProductSchema.PK + ", " + ProductSchema.FK_CATEGORY + ", " + ProductSchema.NAME +
                ", " + ProductSchema.UNITARY_PRICE + ", " + ProductSchema.QUANTITY + ", " + ProductSchema.PHOTO_UID +
                ", " + ProductSchema.CREATED_AT + ", " + ProductSchema.UPDATED_AT +
                " FROM " +
                ProductSchema.TABLE_ID +
                " GROUP BY " + ProductSchema.PK + ";";

        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {

            pstmt = connection.prepareStatement(sql);
            resultSet = pstmt.executeQuery();

            boolean isProductListEmpty = !resultSet.isBeforeFirst();
            if (isProductListEmpty) { return this.getProducts(); }

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

                this.getProducts().add(product);
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

        return this.getProducts();

    }

    public IDbConnection getDbConnectable() {
        return dbConnectable;
    }

    public void setDbConnectable(IDbConnection dbConnectable) {
        this.dbConnectable = dbConnectable;
    }

    public IInventoryManipulationCallbacks getInventoryManipulationCallbacks() {
        return inventoryManipulationCallbacks;
    }

    public void setInventoryManipulationCallbacks(IInventoryManipulationCallbacks inventoryManipulationCallbacks) {
        this.inventoryManipulationCallbacks = inventoryManipulationCallbacks;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
