package com.devldots.inventorymanagement.Interfaces;

import com.devldots.inventorymanagement.Models.Category;
import com.devldots.inventorymanagement.Models.Product;

import java.util.List;

public interface IInventoryManipulationCallbacks {
    public void handleCategoryList(List<Category> categories);
    public void handleProductList(List<Product> products);
}
