package com.devldots.inventorymanagement.DataTransferObjects;

import com.devldots.inventorymanagement.Models.Category;

public class ProductDTO {

    private String idProduct;
    private String name;
    private String unitaryPrice;
    private String quantity;
    private String imagePath;
    private String createdAt;
    private String updatedAt;

    private CategoryDTO category;

    public ProductDTO() {
        this.category = new CategoryDTO();
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitaryPrice() {
        return unitaryPrice;
    }

    public void setUnitaryPrice(String unitaryPrice) {
        this.unitaryPrice = unitaryPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }
}
