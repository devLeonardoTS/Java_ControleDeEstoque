package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.Interfaces.IDataAccessObject;
import com.devldots.inventorymanagement.Models.Product;

import java.util.Collection;

public class ProductService {

    private IDataAccessObject<Product> productDao;

    public ProductService(IDataAccessObject<Product> productDao){
        this.productDao = productDao;
    }

    public boolean saveProduct(Product product) {
        // Todo: Validate the product before interacting with ProductDAO.
        return false;
    }

    public Product getProduct(int id) throws NullPointerException {
        return null;
    }

    public Collection<Product> getProducts() {
        return productDao.getAll();
    }

    public Product updateProduct(Product product) throws NullPointerException {
        return null;
    }

    public boolean deleteProduct(int id) {
        return false;
    }

    private void validate(Product product){
        // Todo: Product data validation logic.
    }
}
