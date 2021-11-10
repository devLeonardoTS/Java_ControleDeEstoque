package com.devldots.inventorymanagement.Interfaces;

import java.util.List;

public interface IImageHandler {
    boolean saveImageToTemp(String selectedImagePath, String imageUid, int maxImageSize);
    boolean removeImageFromTemp(String imageUid);
    boolean persistImage(String imageUid);
    boolean removeImage(String imageUid);
    List<String> getErrorList();
    void setErrorList(List<String> errorList);
}
