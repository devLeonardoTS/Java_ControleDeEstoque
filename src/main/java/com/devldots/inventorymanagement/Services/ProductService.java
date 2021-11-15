package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.Interfaces.IDataAccessHandler;
import com.devldots.inventorymanagement.Models.Product;
import com.devldots.inventorymanagement.Utils.ProductImageHandler;

import java.util.ArrayList;
import java.util.List;

public class ProductService {

    private IDataAccessHandler<Product> productDao;
    private List<String> errorList;

    public ProductService(IDataAccessHandler<Product> productDao){
        this.productDao = productDao;
        this.errorList = new ArrayList<>();
    }

    public boolean saveProduct(Product validatedProduct, String selectedProductImagePath){

        ProductImageHandler productImageHandler = null;

        boolean hasNewImage = selectedProductImagePath != null && !selectedProductImagePath.isBlank();

        if (hasNewImage){
            int imageSize = 125;
            productImageHandler = new ProductImageHandler();
            boolean isImageStored = productImageHandler.saveImageToTemp(selectedProductImagePath, validatedProduct.getImageUid(), imageSize);
            if (!isImageStored){
                this.getErrorList().addAll(productImageHandler.getErrorList());
                return false;
            }
        }

        boolean isProductDataStored = this.getProductDao().save(validatedProduct);

        if (!isProductDataStored){

            if (hasNewImage){
                boolean isProductImageRemoved = productImageHandler.removeImageFromTemp(validatedProduct.getImageUid());
                if (!isProductImageRemoved){
                    this.getErrorList().addAll(productImageHandler.getErrorList());
                }
            }

            this.getErrorList().addAll(this.getProductDao().getErrorList());
            return false;
        }

        if (hasNewImage && isProductDataStored) {
            boolean isImagePersisted = productImageHandler.persistImage(validatedProduct.getImageUid());
            if (!isImagePersisted){
                this.getErrorList().add("Exceto pela foto, os dados do produto foram gravados, isso geralmente indica um problema, por favor siga as instruções da(s) mensagem(ns) abaixo.");
                this.getErrorList().addAll(productImageHandler.getErrorList());
                return false;
            }
        }

        return true;

    }

    public Product getProduct(int id) throws NullPointerException {
        // Todo: ProductService - getProduct(int id);
        return null;
    }

    public List<Product> getProducts() {
        return productDao.getAll();
    }

    public boolean updateProduct(Product validatedProduct, String selectedProductImagePath){

        ProductImageHandler productImageHandler = null;

        boolean hasDefaultImage = validatedProduct.getImageUid() != null && !validatedProduct.getImageUid().isBlank() && validatedProduct.getImageUid().equals(AppConfig.DEFAULT_PRODUCT_IMG_FILE_NAME);
        boolean hasNewImage = !hasDefaultImage && selectedProductImagePath != null && !selectedProductImagePath.isBlank() && !selectedProductImagePath.contains(validatedProduct.getImageUid());

        if (hasNewImage){
            int imageSize = 125;
            productImageHandler = new ProductImageHandler();
            boolean isImageStored = productImageHandler.saveImageToTemp(selectedProductImagePath, validatedProduct.getImageUid(), imageSize);
            if (!isImageStored){
                this.getErrorList().addAll(productImageHandler.getErrorList());
                return false;
            }
        }

        boolean isProductDataUpdated = false;

        boolean isReturningImageToDefault = !hasDefaultImage && (selectedProductImagePath == null || selectedProductImagePath.isBlank());

        String productImageUidReference = null;
        if (isReturningImageToDefault){
            if (productImageHandler == null){
                productImageHandler = new ProductImageHandler();
            }
            productImageUidReference = validatedProduct.getImageUid();
            validatedProduct.setImageUid(null);

            isProductDataUpdated = this.getProductDao().update(validatedProduct);

        }
        if (!isReturningImageToDefault) {

            isProductDataUpdated = this.getProductDao().update(validatedProduct);

        }

        if (!isProductDataUpdated){
            if (hasNewImage){
                boolean isProductImageRemoved = productImageHandler.removeImageFromTemp(validatedProduct.getImageUid());
                if (!isProductImageRemoved){
                    this.getErrorList().addAll(productImageHandler.getErrorList());
                }
            }

            this.getErrorList().addAll(this.getProductDao().getErrorList());
            return false;
        }

        if (hasNewImage) {
            boolean isImagePersisted = productImageHandler.persistImage(validatedProduct.getImageUid());
            if (!isImagePersisted){
                this.getErrorList().add("Exceto pela foto, os dados do produto foram atualizados, isso geralmente indica um problema, por favor siga as instruções da(s) mensagem(ns) abaixo.");
                this.getErrorList().addAll(productImageHandler.getErrorList());
                return false;
            }
        }

        if (isReturningImageToDefault && productImageUidReference != null){
            productImageHandler.removeImage(productImageUidReference);
            if (!productImageHandler.getErrorList().isEmpty()){
                this.getErrorList().add("Os dados do produto foram atualizados mas houve uma falha ao remover a foto do produto, isso geralmente indica um problema, por favor siga as instruções da(s) mensagem(ns) abaixo.");
                this.getErrorList().addAll(productImageHandler.getErrorList());
                return false;
            }
        }

        return true;
    }

    public boolean deleteProduct(Product productToRemove) {
        // Todo: ProductService - deleteProduct(Product productToRemove);

        // [ ] - W.I.P...

        ProductImageHandler productImageHandler = null;

        try {
            boolean isProductRemoved = this.getProductDao().delete(productToRemove.getIdProduct());

            if (!isProductRemoved) {
                this.getErrorList().addAll(this.getProductDao().getErrorList());
                return false;
            }

        } catch (IllegalArgumentException ex){
            this.getErrorList().add("Falha ao remover os dados do produto. Por favor entre em contato com o administrador com a seguinte mensagem: " + this.getClass().getSimpleName() + " - " + ex.getMessage());
            return false;
        }

        boolean hasDefaultImage = productToRemove.getImageUid() != null && !productToRemove.getImageUid().isBlank() && productToRemove.getImageUid().equals(AppConfig.DEFAULT_PRODUCT_IMG_FILE_NAME);

        if (!hasDefaultImage){
            productImageHandler = new ProductImageHandler();
            boolean isImageRemoved = productImageHandler.removeImage(productToRemove.getImageUid());
            if (!isImageRemoved){
                this.getErrorList().addAll(productImageHandler.getErrorList());
                return false;
            }
        }

        return true;
    }

    public IDataAccessHandler<Product> getProductDao() {
        return productDao;
    }

    public void setProductDao(IDataAccessHandler<Product> productDao) {
        this.productDao = productDao;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

}
