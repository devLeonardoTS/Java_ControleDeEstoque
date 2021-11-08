package com.devldots.inventorymanagement.Services;

import com.devldots.inventorymanagement.Abstracts.AbstractDataEntryValidation;
import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.DataTransferObjects.ProductDTO;
import com.devldots.inventorymanagement.Interfaces.IDataAccessObject;
import com.devldots.inventorymanagement.Models.Product;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ProductService {

    private IDataAccessObject<Product> productDao;
    private AbstractDataEntryValidation<ProductDTO, Product> productValidator;
    private List<String> errorList;

    public ProductService(IDataAccessObject<Product> productDao){
        this.productDao = productDao;
        this.errorList = new ArrayList<>();
    }

    public boolean saveProduct(Product validatedProduct, String selectedProductImagePath){

        if (selectedProductImagePath != null && !selectedProductImagePath.isBlank()) {
            int imageSize = 150;
            String newProductImageUUID = saveProductImageToDisk(selectedProductImagePath, imageSize, this.getErrorList());
            if (newProductImageUUID == null){ return false; }
            validatedProduct.setImageUid(newProductImageUUID);
        }

        if (!this.getErrorList().isEmpty()){
            if (validatedProduct.getImageUid() != null) {
                removeImageFromDisk(validatedProduct.getImageUid(), this.getErrorList());
            }
            return false;
        }

        boolean isProductStored = this.getProductDao().save(validatedProduct);

        if (!isProductStored){
            if (validatedProduct.getImageUid() != null) {
                removeImageFromDisk(validatedProduct.getImageUid(), this.getErrorList());
            }
            this.getErrorList().add("Failed to store the product into the database. Please contact the administrator with the following message: "  + this.getClass().getSimpleName() + " - Service failed to add product into the database.");
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

    private String saveProductImageToDisk(String selectedProductImagePath, int targetedImgSize, List<String> errorMsgList){
        if (selectedProductImagePath == null || selectedProductImagePath.isBlank()){
            errorMsgList.add("Failed to process product's image. Please contact the administrator with the following message: " + this.getClass().getSimpleName() + " - Received product image's path as null.");
            return null;
        }

        BufferedImage imageFromPathBuffer = null;
        try {
            imageFromPathBuffer = ImageIO.read(new File(selectedProductImagePath));
        } catch (IOException ex){
            errorMsgList.add("Couldn't find the image file, are you sure the image still exists?");
            return null;
        }

        BufferedImage resizedImageBuffer = null;
        if (imageFromPathBuffer != null) {
            try {
                resizedImageBuffer = Scalr.resize(imageFromPathBuffer, Scalr.Method.ULTRA_QUALITY, targetedImgSize);
            } catch (ImagingOpException | IllegalArgumentException ex) {
                errorMsgList.add("Failed to process product's image. Please contact the administrator with the following message: "  + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                return null;
            }
        }

        String imageExtension = FilenameUtils.getExtension(selectedProductImagePath);

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
                return null;
            }
        } catch (IOException ex){
            errorMsgList.add("Failed to process product's image. Please contact the administrator with the following message: "  + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            return null;
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

    private String parseProductToUserVerifiableString(Product validatedProduct, List<String> errorMsgList){

        String productData = "";

        productData += "• Name: " + validatedProduct.getName() + "\n";
        productData += "• Price: ";
        try {
            DecimalFormat monetaryFormatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.getDefault());
            String monetarySymbol = monetaryFormatter.getCurrency().getSymbol(Locale.getDefault());
            monetaryFormatter.setNegativePrefix(monetarySymbol + " -");
            monetaryFormatter.setMinimumIntegerDigits(1);
            monetaryFormatter.setMinimumFractionDigits(2);
            monetaryFormatter.setMaximumFractionDigits(2);

            productData += monetaryFormatter.format(validatedProduct.getUnitaryPrice()) + "\n";
        } catch (Exception ex){
            errorMsgList.add("Failed to display product entry data and confirmation. Please contact the administrator with the following message: "  + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }

        productData += "• Quantity: " + validatedProduct.getQuantity() + "\n";
        productData += "• Category: " + validatedProduct.getCategory().getName() + "\n";
        productData += "• Has custom image? " + (validatedProduct.getImageUid().isBlank() ? "No" : "Yes") + "\n";

        return productData;
    }

}
