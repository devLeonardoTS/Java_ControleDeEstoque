package com.devldots.inventorymanagement.Controllers;

import com.devldots.inventorymanagement.Components.CBoxBtnCellWithPromptText;
import com.devldots.inventorymanagement.Components.TableCellWithDateFormat;
import com.devldots.inventorymanagement.Components.TableCellWithMonetaryFormat;
import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.Factory.SQLiteConnection;
import com.devldots.inventorymanagement.Interfaces.IInventoryManipulationCallbacks;
import com.devldots.inventorymanagement.Models.Category;
import com.devldots.inventorymanagement.Models.Product;
import com.devldots.inventorymanagement.Services.GetProductCategoriesService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class InventoryController implements IInventoryManipulationCallbacks {

    @FXML private AnchorPane apMain;

    @FXML private Label lblProductName;
    @FXML private Label lblProductUnitaryPrice;
    @FXML private Label lblProductQuantity;
    @FXML private Label lblProductCategory;

    @FXML private TextField txtProductName;
    @FXML private TextField txtProductUnitaryPrice;
    @FXML private TextField txtProductQuantity;
    @FXML private ComboBox<Category> cboProductCategory;

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

    private boolean isProductOperationsEnabled = false;

    @FXML private void initialize(){

        customizeComponents();

        setOnPressEscapeHandler();

        new GetProductCategoriesService(new SQLiteConnection(),this).execute();

        // Todo: Populate Products TableView with products data.
        // 1. Prepare product retrieval callback in interface.
        // 2. Prepare non-blocking product retrieval service.
        // 3. Consume service filling products table view.
    }

    @FXML private void quickLilTest(ActionEvent ev) {
        // this.resetProductImg();

    }

    @FXML private void registerNewProduct(ActionEvent ev) {
        this.enableProductOperation();
    }

    @FXML private void editProduct(ActionEvent ev) { }

    @FXML private void removeProduct(ActionEvent ev) { }

    @FXML private void cancelProductOperations(ActionEvent ev) {
        this.resetControls();
    }

    @FXML private void productImgSelectionHandler(ActionEvent ev) { }

    @FXML private void productSelectionHandler(ActionEvent ev) { }

    private void resetControls(){
        this.isProductOperationsEnabled = false;

        this.btnRegister.setDisable(false);
        this.btnRegister.setText("Novo Prod.");

        this.btnUpdate.setDisable(true);
        this.btnUpdate.setText("Atualizar");

        this.btnCancel.setDisable(true);
        this.btnDelete.setDisable(true);

        this.txtProductName.setDisable(true);
        this.txtProductName.clear();

        this.txtProductUnitaryPrice.setDisable(true);
        this.txtProductUnitaryPrice.clear();

        this.txtProductQuantity.setDisable(true);
        this.txtProductQuantity.clear();

        this.cboProductCategory.setDisable(true);
        this.cboProductCategory.getSelectionModel().clearSelection();
        this.cboProductCategory.valueProperty().setValue(null);

        this.tblProducts.setDisable(false);
        this.tblProducts.getSelectionModel().clearSelection();

        this.clipProductImg.setStroke(Paint.valueOf("#0000003f"));

        this.lblChangeProductImg.setVisible(false);

        this.resetProductImg();
    };

    private void resetProductImg(){
        InputStream productImgInputStream = this.getClass().getClassLoader().getResourceAsStream(AppConfig.DEFAULT_PRODUCT_IMG_RESOURCE_PATH);
        if (productImgInputStream != null){
            Image defaultProductImg = new Image(productImgInputStream);
            this.imgvProductImg.setClip(this.clipProductImg.getClip());
            this.imgvProductImg.setImage(defaultProductImg);
        }
    }

    private void setOnPressEscapeHandler(){
        this.apMain.setOnKeyPressed(eh -> {
            if (eh.getCode() == KeyCode.ESCAPE){
                boolean isProductSelected = !this.tblProducts.getSelectionModel().isEmpty();

                if (isProductSelected){
                    this.resetControls();
                }
            }
        });
    }

    private void enableProductOperation(){

        this.isProductOperationsEnabled = true;

        this.btnCancel.setDisable(false);
        this.btnDelete.setDisable(true);

        this.tblProducts.setDisable(true);

        this.txtProductName.setDisable(false);
        this.txtProductUnitaryPrice.setDisable(false);
        this.txtProductQuantity.setDisable(false);
        this.cboProductCategory.setDisable(false);

        this.clipProductImg.setStroke(Color.BLUE);

        this.lblChangeProductImg.setVisible(true);

    }

    private void customizeComponents(){

        this.cboProductCategory.setButtonCell(new CBoxBtnCellWithPromptText("Selecione uma categoria"));

        this.tblColProductId.setCellValueFactory(new PropertyValueFactory<>("id"));

        this.tblColProductName.setCellValueFactory(new PropertyValueFactory<>("name"));

        this.tblColProductUnitaryPrice.setCellValueFactory(new PropertyValueFactory<>("unitaryPrice"));
        this.tblColProductUnitaryPrice.setCellFactory(col -> new TableCellWithMonetaryFormat<>(new Locale("pt", "BR"), false));

        this.tblColProductCategory.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        this.tblColProductCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        this.tblColProductRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        this.tblColProductRegistrationDate.setCellFactory(col -> new TableCellWithDateFormat<>("dd/MM/yyyy HH:mm:ss"));

        this.tblColProductUpdateDate.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        this.tblColProductUpdateDate.setCellFactory(col -> new TableCellWithDateFormat<>("dd/MM/yyyy HH:mm:ss"));

    }

    @Override
    public void handleCategoryList(List<Category> categories) {
        List<Category> productCategories = this.cboProductCategory.getItems();

        if (productCategories.isEmpty()) {
            for (Category item : categories) {
                productCategories.add(item);
            }
        }
    }

    @Override
    public void handleProductList(List<Product> products) {
        // Todo: Insert products into Product TableView.
    }
}
