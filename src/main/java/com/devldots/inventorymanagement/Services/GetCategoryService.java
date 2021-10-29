package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.Constants.CategorySchema;
import com.devldots.inventorymanagement.Factory.IDbConnection;
import com.devldots.inventorymanagement.Models.Category;
import com.devldots.inventorymanagement.Models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetCategoryService {

    private IDbConnection dbConnectable;
    private Category category;

    private int idCategory;

    public GetCategoryService(IDbConnection dbConnectable, Category category, int idCategory){
        this.dbConnectable = dbConnectable;
        this.idCategory = idCategory;
        this.category = category;
    }

    public void execute(){
        Thread thread = new Thread(() -> {
            category = fetchCategory();
        });
        thread.start();
    }

    public Category fetchCategory(){

        Connection connection = this.dbConnectable.getConnection();

        String sql = "SELECT * FROM " + CategorySchema.TABLE_ID +
                " WHERE " + CategorySchema.PK + " = ?;";

        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = connection.prepareStatement(sql);

            pstmt.setInt(1, this.idCategory);

            resultSet = pstmt.executeQuery();

            boolean isCategoryNotFound = !resultSet.isBeforeFirst();
            if (isCategoryNotFound) { return null; }

            while (resultSet.next()){
                this.category = new Category();
                this.category.setIdCategory(resultSet.getInt(CategorySchema.PK));
                this.category.setName(resultSet.getString(CategorySchema.NAME));
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

        return this.category;
    }


}
