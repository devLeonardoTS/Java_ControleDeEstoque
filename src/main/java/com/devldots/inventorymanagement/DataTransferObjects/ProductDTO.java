package com.devldots.inventorymanagement.DataTransferObjects;

import com.devldots.inventorymanagement.Models.Category;

public class ProductDTO {

    private String name;
    private String unitaryPrice;
    private String quantity;
    private Category category;
    private String imageUid;

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUid() {
        return imageUid;
    }

    public void setImageUid(String imageUid) {
        this.imageUid = imageUid;
    }
}
