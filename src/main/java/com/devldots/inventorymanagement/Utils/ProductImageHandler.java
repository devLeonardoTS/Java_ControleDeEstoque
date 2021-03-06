package com.devldots.inventorymanagement.Utils;

import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.Interfaces.IImageHandler;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ProductImageHandler implements IImageHandler {

    private List<String> errorList;

    public ProductImageHandler(){
        this.errorList = new ArrayList<>();
    }

    @Override
    public boolean saveImageToTemp(String selectedImagePath, String imageUid, int maxImageSize) {

        if (selectedImagePath == null || selectedImagePath.isBlank()){
            this.getErrorList().add("Falha ao processar a imagem do produto. Por favor entre em contato com o administrador com a seguinte mensagem: " + this.getClass().getSimpleName() + " - Received product image's path as null or empty.");
            return false;
        }

        BufferedImage imageFromPathBuffer = null;
        try {
            imageFromPathBuffer = ImageIO.read(new File(selectedImagePath));
        } catch (IOException ex){
            this.getErrorList().add("Não foi possível encontrar o arquivo da imagem, você tem certeza que o arquivo ainda existe?");
            return false;
        }

        BufferedImage resizedImageBuffer = null;
        if (imageFromPathBuffer != null) {
            try {
                resizedImageBuffer = Scalr.resize(imageFromPathBuffer, Scalr.Method.ULTRA_QUALITY, maxImageSize);
            } catch (ImagingOpException | IllegalArgumentException ex) {
                this.getErrorList().add("Falha ao processar a imagem do produto. Por favor entre em contato com o administrador com a seguinte mensagem: "  + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                return false;
            }
        }

        String resizedImageDestination = Path.of(AppConfig.TEMP_DIR, imageUid).toString();

        File resizedImageFile = new File(resizedImageDestination);

        try {
            boolean isImageProcessingComplete = ImageIO.write(resizedImageBuffer, FilenameUtils.getExtension(imageUid), resizedImageFile);
            if (!isImageProcessingComplete){
                this.getErrorList().add("Falha ao processar a imagem do produto. A imagem deve estar em um desses formatos: \".jpg\", \".jpeg\" or \".png\".");
                return false;
            }
        } catch (IOException ex){
            this.getErrorList().add("Falha ao processar a imagem do produto. Por favor entre em contato com o administrador com a seguinte mensagem: "  + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            return false;
        }

        return true;

    }

    @Override
    public boolean removeImageFromTemp(String imageUid) {

        String productImagePath = Path.of(AppConfig.TEMP_DIR, imageUid).toString();

        File imageToRemove = new File(productImagePath);

        if (imageToRemove.exists()){
            imageToRemove.delete();
        }

        return true;

    }

    @Override
    public boolean persistImage(String imageUid) {

        String temporaryProductImagePath = Path.of(AppConfig.TEMP_DIR, imageUid).toString();
        String persistedProductImagePath = Path.of(AppConfig.PRODUCT_IMG_DIR, imageUid).toString();

        File imageToPersist = new File(temporaryProductImagePath);

        if (imageToPersist.exists()){
            try {
                Files.move(Path.of(temporaryProductImagePath), Path.of(persistedProductImagePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex){
                this.getErrorList().add("Falha ao armazenar a imagem processada do produto. Por favor entre em contato com o administrador com a seguinte mensagem: "  + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean removeImage(String imageUid) {

        String productImagePath = Path.of(AppConfig.PRODUCT_IMG_DIR, imageUid).toString();

        File imageToRemove = new File(productImagePath);

        if (imageToRemove.exists()){
            boolean isRemovalSuccessful = imageToRemove.delete();
            if (!isRemovalSuccessful){
                this.getErrorList().add("Falha ao remover a seguinte imagem de um produto: " + imageUid);
                return false;
            }
        }

        return true;

    }

    @Override
    public List<String> getErrorList() {
        return this.errorList;
    }

    @Override
    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

}
