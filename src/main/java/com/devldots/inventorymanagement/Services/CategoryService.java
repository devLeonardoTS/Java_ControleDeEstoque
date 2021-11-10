package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.Interfaces.IDataAccessHandler;
import com.devldots.inventorymanagement.Models.Category;

import java.util.List;

public class CategoryService {

    private IDataAccessHandler<Category> categoryDao;
    private List<String> errorList;

    public CategoryService(IDataAccessHandler<Category> categoryDao){
        this.categoryDao = categoryDao;
    }

    public boolean saveCategory(Category validatedCategory){
        // Todo: Category creation validation and logic.
        return false;
    }

    public Category getCategory(int id) throws NullPointerException {
        // Todo: CategoryService - getCategory(int id);
        return categoryDao.get(id);
    }

    public List<Category> getCategories(){
        return categoryDao.getAll();
    }

    public boolean updateCategory(Category validatedCategory) throws NullPointerException {
        // Todo: CategoryService - updateCategory(Category validatedCategory);
        return false;
    }

    public boolean deleteCategory(int id) {
        // Todo: CategoryService - deleteCategory(int id);
        return false;
    }

    public IDataAccessHandler<Category> getCategoryDao() {
        return categoryDao;
    }

    public void setCategoryDao(IDataAccessHandler<Category> categoryDao) {
        this.categoryDao = categoryDao;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

}
