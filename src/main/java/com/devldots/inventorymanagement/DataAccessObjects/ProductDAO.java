package com.devldots.inventorymanagement.DataAccessObjects;

import com.devldots.inventorymanagement.Constants.ProductSchema;
import com.devldots.inventorymanagement.Interfaces.IDataAccessObject;
import com.devldots.inventorymanagement.Interfaces.IDbConnection;
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

    IDbConnection dbConnectable;

    public ProductDAO(IDbConnection dbConnectable){
        this.dbConnectable = dbConnectable;
    }

    @Override
    public boolean save(Product object) {
        // Todo: New product registration SQL.
        return false;
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
    public Product update(Product object) throws NullPointerException {
        return null;
    }

    @Override
    public boolean delete(Object id) throws IllegalArgumentException {
        return false;
    }
}
