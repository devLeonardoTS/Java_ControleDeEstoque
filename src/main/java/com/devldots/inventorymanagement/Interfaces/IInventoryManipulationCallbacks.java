package com.devldots.inventorymanagement.Interfaces;

import com.devldots.inventorymanagement.Models.Category;

import java.util.List;

public interface IInventoryManipulationCallbacks {
    public void handleCategoryList(List<Category> categories);
}
