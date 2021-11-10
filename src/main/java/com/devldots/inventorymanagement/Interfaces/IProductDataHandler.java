package com.devldots.inventorymanagement.Interfaces;

import com.devldots.inventorymanagement.Models.Product;

import java.util.List;

public interface IProductDataHandler {

    boolean save(Product validatedProduct);

    Product get(Object id) throws NullPointerException, IllegalArgumentException;

    List<Product> getAll();

    boolean update(Product validatedProduct);

    boolean delete (Object id) throws IllegalArgumentException;

    List<String> getErrorList();
    void setErrorList(List<String> errorList);
}
