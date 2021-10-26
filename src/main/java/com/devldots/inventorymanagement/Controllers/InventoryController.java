package com.devldots.inventorymanagement.Controllers;

import com.devldots.inventorymanagement.Models.Category;
import com.devldots.inventorymanagement.Models.Product;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryController {

    @FXML private Label lblProductName;
    @FXML private Label lblProductUnitaryPrice;
    @FXML private Label lblProductQuantity;
    @FXML private Label lblProductCategory;

    @FXML private TextField txtProductName;
    @FXML private TextField txtProductUnitaryPrice;
    @FXML private TextField txtProductQuantity;
    @FXML private ComboBox cboProductCategory;

    @FXML private Label lblChangeProductImg;
    @FXML private Rectangle clipProductImg;
    @FXML private ImageView imgvProductImg;

    @FXML private Rectangle clipCompanyLogoImg;
    @FXML private ImageView imgvCompanyLogoImg;

    @FXML private Button btnRegister;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnCancel;
    @FXML private Button btnLogout;

    @FXML private TableView<Product> tblProducts;
    @FXML private TableColumn<Product, Integer> tblColProductId;
    @FXML private TableColumn<Product, String> tblColProductName;
    @FXML private TableColumn<Product, BigDecimal> tblColProductUnitaryPrice;
    @FXML private TableColumn<Product, Integer> tblColProductQuantity;
    @FXML private TableColumn<Product, Category> tblColProductCategory;
    @FXML private TableColumn<Product, LocalDateTime> tblColProductRegistrationDate;
    @FXML private TableColumn<Product, LocalDateTime> tblColProductUpdateDate;

    @FXML public void quickLilTest() { }

    @FXML public void registerNewProduct() { }

    @FXML public void editProduct() { }

    @FXML public void removeProduct() { }

    @FXML public void cancelProductOperations() { }

    @FXML public void productImgSelectionHandler() { }

    @FXML public void productSelectionHandler() { }

}
