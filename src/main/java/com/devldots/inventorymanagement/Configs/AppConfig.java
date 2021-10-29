package com.devldots.inventorymanagement.Configs;

import java.io.File;
import java.nio.file.Path;

public final class AppConfig {
    public final static String WORKING_DIR = Path.of(System.getProperty("user.dir")).toString();
    public final static String FILES_DIR = Path.of(WORKING_DIR, "files_storage").toString();
    public final static String IMGS_DIR = Path.of(FILES_DIR, "Images").toString();
    public final static String PRODUCT_IMG_DIR = Path.of(IMGS_DIR, "Products").toString();
    public final static String LOGS_DIR = Path.of(FILES_DIR, "Logs").toString();


    public final static String DEFAULT_PRODUCT_IMG_RESOURCE_PATH = "Assets/default_product_img.png";
    public final static String DEFAULT_COMPANY_LOGO_RESOURCE_PATH = "Assets/default_company_logo.png";

    public final static String DEFAULT_PRODUCT_IMG_FILE_NAME = "default_product_img.png";
    public final static String DEFAULT_COMPANY_LOGO_FILE_NAME = "default_company_logo.png";

    public final static String DEFAULT_DB_PATH = Path.of(WORKING_DIR, "database.db").toString();

    // private static String dbUsername = "root";
    // private static String dbPassword = "";

    // public static String getDbUsername() {
    //     return dbUsername;
    // }

    // public static String getDbPassword() {
    //     return dbPassword;
    // }

    public static void createFileDirectories() {

        new File(PRODUCT_IMG_DIR).mkdirs();
        new File(LOGS_DIR).mkdirs();
    }

}
