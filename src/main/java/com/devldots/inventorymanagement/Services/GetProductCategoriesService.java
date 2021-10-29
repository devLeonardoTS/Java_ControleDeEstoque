package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.Constants.CategorySchema;
import com.devldots.inventorymanagement.Interfaces.IInventoryManipulationCallbacks;
import com.devldots.inventorymanagement.Factory.IDbConnection;
import com.devldots.inventorymanagement.Models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GetProductCategoriesService {

    private IDbConnection dbConnectable;
    private IInventoryManipulationCallbacks inventoryManipulationCallbacks;

    private List<Category> categoryList;

    public GetProductCategoriesService(IDbConnection dbConnectable, IInventoryManipulationCallbacks inventoryManipulationCbs) {
        this.dbConnectable = dbConnectable;
        this.inventoryManipulationCallbacks = inventoryManipulationCbs;
        this.categoryList = new ArrayList<>();
    }

    public void execute(){
        Thread thread = new Thread(() -> {
            inventoryManipulationCallbacks.handleCategoryList(fetchCategories());
        });
        thread.start();
    }

    private List<Category> fetchCategories(){

        Connection connection = this.dbConnectable.getConnection();

        String sql = "SELECT" +
                " " + CategorySchema.PK + ", " + CategorySchema.NAME +
                " FROM " + CategorySchema.TABLE_ID +
                " ORDER BY " + CategorySchema.PK + ";";

        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {

            pstmt = connection.prepareStatement(sql);
            resultSet = pstmt.executeQuery();

            boolean isCategoryListEmpty = !resultSet.isBeforeFirst();
            if (isCategoryListEmpty) { return this.getCategoryList(); }

            while (resultSet.next()) {
                Category category = new Category();
                category.setIdCategory(resultSet.getInt(CategorySchema.PK));
                category.setName(resultSet.getString(CategorySchema.NAME));

                this.getCategoryList().add(category);
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

        return this.getCategoryList();
    }

    public IDbConnection getDbConnectable() {
        return dbConnectable;
    }

    public void setDbConnectable(IDbConnection dbConnectable) {
        this.dbConnectable = dbConnectable;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public IInventoryManipulationCallbacks getInventoryManipulationCallbacks() {
        return inventoryManipulationCallbacks;
    }

    public void setInventoryManipulationCallbacks(IInventoryManipulationCallbacks inventoryManipulationCallbacks) {
        this.inventoryManipulationCallbacks = inventoryManipulationCallbacks;
    }
}
