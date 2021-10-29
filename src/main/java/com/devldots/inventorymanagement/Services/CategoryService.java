package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.DataTransferObjects.CategoryDTO;
import com.devldots.inventorymanagement.Interfaces.IDataAccessObject;
import com.devldots.inventorymanagement.Models.Category;

import java.util.Collection;

public class CategoryService {

    private IDataAccessObject<Category> categoryDao;

    public CategoryService(IDataAccessObject<Category> categoryDao){
        this.categoryDao = categoryDao;
    }

    public boolean saveCategory(Category category){
        return false;
    }

    public Category getCategory(int id) throws NullPointerException {
        return categoryDao.get(id);
    }

    public Collection<Category> getCategories(){
        return categoryDao.getAll();
    }

    public Category updateCategory(Category category) throws NullPointerException {
        return null;
    }

    public boolean deleteCategory(int id) {
        return false;
    }

    private void validate(Category category){
        // Todo: Category validation.
    };

}
