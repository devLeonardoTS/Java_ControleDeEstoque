package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.Interfaces.IDataAccessObject;
import com.devldots.inventorymanagement.Models.Product;

import java.util.List;

public class ProductService {

    private IDataAccessObject<Product> productDao;

    public ProductService(IDataAccessObject<Product> productDao){
        this.productDao = productDao;
    }

    public boolean saveProduct(Product validatedProduct) {
        this.productDao.save(validatedProduct);
        return false;
    }

    public Product getProduct(int id) throws NullPointerException {
        return null;
    }

    public List<Product> getProducts() {
        return productDao.getAll();
    }

    public Product updateProduct(Product product) throws NullPointerException {
        return null;
    }

    public boolean deleteProduct(int id) {
        return false;
    }

}
