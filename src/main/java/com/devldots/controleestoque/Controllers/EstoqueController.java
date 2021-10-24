package com.devldots.controleestoque.Controllers;

import com.devldots.controleestoque.Models.Categoria;
import com.devldots.controleestoque.Models.Produto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EstoqueController {

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

    @FXML private TableView<Produto> tblProducts;
    @FXML private TableColumn<Produto, Integer> tblColProductId;
    @FXML private TableColumn<Produto, String> tblColProductName;
    @FXML private TableColumn<Produto, BigDecimal> tblColProductUnitaryPrice;
    @FXML private TableColumn<Produto, Integer> tblColProductQuantity;
    @FXML private TableColumn<Produto, Categoria> tblColProductCategory;
    @FXML private TableColumn<Produto, LocalDateTime> tblColProductRegistrationDate;
    @FXML private TableColumn<Produto, LocalDateTime> tblColProductUpdateDate;

    @FXML public void quickLilTest() { }

    @FXML public void registerNewProduct() { }

    @FXML public void editProduct() { }

    @FXML public void removeProduct() { }

    @FXML public void cancelProductOperations() { }

    @FXML public void productImgSelectionHandler() { }

    @FXML public void productSelectionHandler() { }

}
