package com.devldots.inventorymanagement.Utils;

import com.devldots.inventorymanagement.Abstracts.AbstractDataEntryValidation;
import com.devldots.inventorymanagement.DataTransferObjects.ProductDTO;
import com.devldots.inventorymanagement.Models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductValidation extends AbstractDataEntryValidation<ProductDTO, Product> {

    public ProductValidation(List<String> errorList) {
        super(errorList);
    }

    public ProductValidation(){
        super(new ArrayList<String>());
    }

    @Override
    public boolean validate(ProductDTO productInput) {
        // Todo: Validate the product entry.
        // 1. [ ] Set up the logic for validating the required fields.
        System.out.println("ID: " + productInput.getIdProduct());
        System.out.println("Product name: " + productInput.getName());
        System.out.println("Product price: " + productInput.getUnitaryPrice());
        System.out.println("Product quantity: " + productInput.getQuantity());
        System.out.println("Product categoryId: " + productInput.getIdCategory());
        System.out.println("Product imageUid: " + productInput.getImageUid());
        System.out.println("Product createdAt: " + productInput.getCreatedAt());
        System.out.println("Product updatedAt: " + productInput.getUpdatedAt());
        return false;
    }

}
