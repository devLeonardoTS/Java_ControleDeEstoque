package com.devldots.inventorymanagement.Configs;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

public final class AppConfig {
    public final static String WORKING_DIR = Path.of(System.getProperty("user.dir")).toString();
    public final static String FILES_DIR = Path.of(WORKING_DIR, "files_storage").toString();
    public final static String IMGS_DIR = Path.of(FILES_DIR, "Images").toString();
    public final static String TEMP_DIR = Path.of(FILES_DIR, "Temp").toString();
    public final static String PRODUCT_IMG_DIR = Path.of(IMGS_DIR, "Products").toString();
    public final static String LOGS_DIR = Path.of(FILES_DIR, "Logs").toString();

    public final static String DEFAULT_PRODUCT_IMG_RESOURCE_PATH = "Assets/default_product_img.png";
    public final static String DEFAULT_COMPANY_LOGO_RESOURCE_PATH = "Assets/default_company_logo.png";

    public final static String DEFAULT_PRODUCT_IMG_FILE_NAME = "default_product_img.png";
    public final static String DEFAULT_COMPANY_LOGO_FILE_NAME = "default_company_logo.png";

    public final static String DEFAULT_DB_PATH = Path.of(WORKING_DIR, "database.db").toString();

    public final static Locale userLocale = new Locale("pt", "BR");

    public static void createFileDirectories() {

        new File(PRODUCT_IMG_DIR).mkdirs();
        new File(LOGS_DIR).mkdirs();
        recursiveDirectoryRemoval(TEMP_DIR);
        new File(TEMP_DIR).mkdirs();

    }

    private static void recursiveDirectoryRemoval(String directoryPath){

        if (!directoryPath.contains(FILES_DIR)){ return; }

        try {
            FileUtils.deleteDirectory(new File(directoryPath));
        } catch (IOException ex){
            System.out.println(ex);
        }

    }

}
