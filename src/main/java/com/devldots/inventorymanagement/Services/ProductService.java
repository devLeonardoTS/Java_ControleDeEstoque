package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.Abstracts.AbstractDataEntryValidation;
import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.DataTransferObjects.ProductDTO;
import com.devldots.inventorymanagement.Interfaces.IDataAccessObject;
import com.devldots.inventorymanagement.Models.Product;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductService {

    private IDataAccessObject<Product> productDao;
    private AbstractDataEntryValidation<ProductDTO, Product> productValidator;
    private List<String> errorList;

    public ProductService(IDataAccessObject<Product> productDao){
        this.productDao = productDao;
        this.errorList = new ArrayList<>();
    }

    public boolean saveProduct(ProductDTO productInput, AbstractDataEntryValidation<ProductDTO, Product> productValidator) {

        this.setProductValidator(productValidator);

        boolean isProductValid = this.getProductValidator().validate(productInput);

        if (!isProductValid){
            this.getErrorList().addAll(this.getProductValidator().getErrorList());
            return false;
        }

        Product newProduct = this.getProductValidator().getValidated();

        int imageSize = 150;
        String newProductImageUUID = saveProductImageToDisk(productInput.getImagePath(), imageSize, this.getErrorList());

        newProduct.setImageUid(newProductImageUUID);

        if (!this.getErrorList().isEmpty()){
            removeImageFromDisk(newProductImageUUID, this.getErrorList());
            return false;
        }

        boolean isProductStored = this.getProductDao().save(newProduct);

        if (!isProductStored){
            removeImageFromDisk(newProductImageUUID, this.getErrorList());
            return false;
        }

        return true;
    }

    public Product getProduct(int id) throws NullPointerException {
        return null;
    }

    public List<Product> getProducts() {
        return productDao.getAll();
    }

    public Product updateProduct(ProductDTO productInput,  AbstractDataEntryValidation<ProductDTO, Product> productValidator) throws NullPointerException {
        this.setProductValidator(productValidator);

        return null;
    }

    public boolean deleteProduct(int id) {
        return false;
    }

    public IDataAccessObject<Product> getProductDao() {
        return productDao;
    }

    public void setProductDao(IDataAccessObject<Product> productDao) {
        this.productDao = productDao;
    }

    public AbstractDataEntryValidation<ProductDTO, Product> getProductValidator() {
        return productValidator;
    }

    public void setProductValidator(AbstractDataEntryValidation<ProductDTO, Product> productValidator) {
        this.productValidator = productValidator;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    private String saveProductImageToDisk(String imagePath, int targetedImgSize, List<String> errorMsgList){
        if (imagePath.isBlank()){ return ""; }

        BufferedImage imageFromPathBuffer = null;
        try {
            imageFromPathBuffer = ImageIO.read(new File(imagePath));
        } catch (IOException ex){
            errorMsgList.add("Couldn't find the image file, are you sure the image still exists?");
            return "";
        }

        BufferedImage resizedImageBuffer = null;
        if (imageFromPathBuffer != null) {
            try {
                resizedImageBuffer = Scalr.resize(imageFromPathBuffer, Scalr.Method.ULTRA_QUALITY, targetedImgSize);
            } catch (ImagingOpException | IllegalArgumentException ex) {
                errorMsgList.add("Failed to process product's image. Please contact the administrator with the following message: "  + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                return "";
            }
        }

        String imageExtension = FilenameUtils.getExtension(imagePath);

        String resizedImageUUID = UUID.randomUUID() + "." + imageExtension;
        String resizedImageDestination = Path.of(AppConfig.PRODUCT_IMG_DIR, resizedImageUUID).toString();

        File resizedImageFile = new File(resizedImageDestination);

        boolean isUUIDTaken = resizedImageFile.exists();
        while (isUUIDTaken){
            resizedImageUUID = UUID.randomUUID() + "." + imageExtension;
            resizedImageDestination = Path.of(AppConfig.PRODUCT_IMG_DIR, resizedImageUUID).toString();
            resizedImageFile = new File(resizedImageDestination);
            isUUIDTaken = resizedImageFile.exists();
        }

        try {
            boolean isImageProcessingComplete = ImageIO.write(resizedImageBuffer, imageExtension, resizedImageFile);
            if (!isImageProcessingComplete){
                errorMsgList.add("Failed to process product's image. The image format must be one of the following: \".jpg\", \".jpeg\" or \".png\".");
                return "";
            }
        } catch (IOException ex){
            errorMsgList.add("Failed to process product's image. Please contact the administrator with the following message: "  + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            return "";
        }

        return resizedImageUUID;

    }

    private boolean removeImageFromDisk(String productImageUUID, List<String> errorMsgList){
        String productImagePath = Path.of(AppConfig.PRODUCT_IMG_DIR, productImageUUID).toString();

        File imageToRemove = new File(productImagePath);

        if (imageToRemove.exists()){
            boolean isRemovalSuccessful = imageToRemove.delete();
            if (!isRemovalSuccessful){
                errorMsgList.add("Failed to remove the following unused product image: " + productImageUUID);
                return false;
            }
        }

        return true;
    }
}
