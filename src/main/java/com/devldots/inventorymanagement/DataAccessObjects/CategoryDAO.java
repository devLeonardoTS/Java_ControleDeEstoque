package com.devldots.inventorymanagement.DataAccessObjects;

import com.devldots.inventorymanagement.Constants.CategorySchema;
import com.devldots.inventorymanagement.Interfaces.IDataAccessObject;
import com.devldots.inventorymanagement.Interfaces.IDbConnection;
import com.devldots.inventorymanagement.Models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class CategoryDAO implements IDataAccessObject<Category> {

    IDbConnection dbConnectable;

    public CategoryDAO(IDbConnection dbConnectable){
        this.dbConnectable = dbConnectable;
    }

    @Override
    public boolean save(Category object) {
        return false;
    }

    @Override
    public Category get(Object id) throws NullPointerException, IllegalArgumentException {

        if (!(id instanceof Integer)){
            throw new IllegalArgumentException("The id sent is of type [" +id.getClass() +"] please provide an Integer.");
        }

        Connection connection = this.dbConnectable.getConnection();

        String sql = "SELECT * FROM " + CategorySchema.TABLE_ID +
                " WHERE " + CategorySchema.PK + " = ?;";

        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        Category category = null;

        try {
            pstmt = connection.prepareStatement(sql);

            pstmt.setInt(1, (Integer) id);

            resultSet = pstmt.executeQuery();

            boolean isCategoryNotFound = !resultSet.isBeforeFirst();

            if (isCategoryNotFound) { return null; }

            while (resultSet.next()){
                category = new Category();

                category.setIdCategory(resultSet.getInt(CategorySchema.PK));
                category.setName(resultSet.getString(CategorySchema.NAME));
            }

        } catch (SQLException ex) {
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

        return category;

    }

    @Override
    public Collection<Category> getAll() {

        Connection connection = this.dbConnectable.getConnection();

        String sql = "SELECT" +
                " " + CategorySchema.PK + ", " + CategorySchema.NAME +
                " FROM " + CategorySchema.TABLE_ID +
                " ORDER BY " + CategorySchema.PK + ";";

        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        Collection<Category> categories = new ArrayList<>();

        try {

            pstmt = connection.prepareStatement(sql);
            resultSet = pstmt.executeQuery();

            boolean isCategoryListEmpty = !resultSet.isBeforeFirst();
            if (isCategoryListEmpty) { return categories; }

            while (resultSet.next()) {
                Category category = new Category();

                category.setIdCategory(resultSet.getInt(CategorySchema.PK));
                category.setName(resultSet.getString(CategorySchema.NAME));

                categories.add(category);
            }

        } catch (SQLException ex){

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

        return categories;

    }

    @Override
    public Category update(Category object) throws NullPointerException {
        return null;
    }

    @Override
    public boolean delete(Object id) throws IllegalArgumentException {
        return false;
    }

}
