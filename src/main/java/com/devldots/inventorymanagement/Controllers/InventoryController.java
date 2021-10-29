package com.devldots.inventorymanagement.Controllers;

import com.devldots.inventorymanagement.Components.CBoxBtnCellWithPromptText;
import com.devldots.inventorymanagement.Components.TableCellWithDateFormat;
import com.devldots.inventorymanagement.Components.TableCellWithMonetaryFormat;
import com.devldots.inventorymanagement.Components.TableCellWithTooltip;
import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.Factory.SQLiteConnection;
import com.devldots.inventorymanagement.Interfaces.IInventoryManipulationCallbacks;
import com.devldots.inventorymanagement.Models.Category;
import com.devldots.inventorymanagement.Models.Product;
import com.devldots.inventorymanagement.Services.GetCategoryService;
import com.devldots.inventorymanagement.Services.GetProductCategoriesService;
import com.devldots.inventorymanagement.Services.GetProductsService;
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

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
        // 1. [X] Prepare product retrieval callback in interface.
        // 2. [ ] Prepare non-blocking product retrieval service.
        // 3. [ ] Consume service filling products table view.
    }

    @FXML private void quickLilTest() {
        // this.resetProductImg();

        new GetProductsService(new SQLiteConnection(), this).execute();

    }

    @FXML private void registerNewProduct() {
        this.enableProductOperation();
    }

    @FXML private void editProduct() { }

    @FXML private void removeProduct() { }

    @FXML private void cancelProductOperations() {
        this.resetControls();
    }

    @FXML private void productImgSelectionHandler() { }

    @FXML private void productSelectionHandler() {

        // Todo: Rework the product selection handler.

        Product selectedProduct = this.tblProducts.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) { return; }

        this.btnUpdate.setDisable(false);
        this.btnDelete.setDisable(false);

        this.fillControls(selectedProduct);

    }

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
            this.centerImage(this.imgvProductImg);
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

        this.cboProductCategory.setButtonCell(new CBoxBtnCellWithPromptText<>("Selecione uma categoria"));

        this.tblColProductId.setCellValueFactory(new PropertyValueFactory<>("idProduct"));
        this.tblColProductId.setCellFactory(col -> new TableCellWithTooltip<>());

        this.tblColProductName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.tblColProductName.setCellFactory(col -> new TableCellWithTooltip<>());

        this.tblColProductUnitaryPrice.setCellValueFactory(new PropertyValueFactory<>("unitaryPrice"));
        this.tblColProductUnitaryPrice.setCellFactory(col -> new TableCellWithMonetaryFormat<>(new Locale("pt", "BR"), false));

        this.tblColProductQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        this.tblColProductQuantity.setCellFactory(col -> new TableCellWithTooltip<>());

        this.tblColProductCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        this.tblColProductCategory.setCellFactory(col -> new TableCellWithTooltip<>());

        this.tblColProductRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        this.tblColProductRegistrationDate.setCellFactory(col -> new TableCellWithDateFormat<>("dd/MM/yyyy HH:mm:ss"));

        this.tblColProductUpdateDate.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        this.tblColProductUpdateDate.setCellFactory(col -> new TableCellWithDateFormat<>("dd/MM/yyyy HH:mm:ss"));

    }

    private void fillControls(Product product){

        this.txtProductName.setText(product.getName());

        Locale locale = new Locale("pt", "BR");
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(locale);
        DecimalFormatSymbols localizedSymbols = new DecimalFormatSymbols(locale);
        df.setDecimalFormatSymbols(localizedSymbols);
        df.setMinimumIntegerDigits(1);
        df.setMinimumFractionDigits(2);
        this.txtProductUnitaryPrice.setText( df.format(product.getUnitaryPrice() ) );

        this.txtProductQuantity.setText(Integer.toString(product.getQuantity()));

        this.cboProductCategory.getSelectionModel().select(product.getCategory());

        String productImgPath = Path.of(AppConfig.PRODUCT_IMG_DIR, product.getImageUid()).toString();
        File imgFile = new File(productImgPath);
        boolean imgFileExists = imgFile.exists();
        if (!imgFileExists) {
            this.resetProductImg();
            return;
        }
        Image productImg = null;
        try {
            productImg = new Image(productImgPath);

            this.imgvProductImg.setClip(this.clipProductImg.getClip());
            this.imgvProductImg.setImage(productImg);
            this.centerImage(this.imgvProductImg);
        } catch (NullPointerException | IllegalArgumentException ex){
            this.resetProductImg();
        }

    }

    private void centerImage(ImageView imageView){
        //  This algorithm is proposed in this SO Post: https://stackoverflow.com/questions/32781362/centering-an-image-in-an-imageview [ 2021/10/29 ]
        Image img = imageView.getImage();
        if (img != null){
            double w = 0;
            double h = 0;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if (ratioX >= ratioY){
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);
        }
    }

    @Override
    public void handleCategoryList(List<Category> categories) {
        List<Category> productCategories = this.cboProductCategory.getItems();

        if (productCategories.isEmpty()) {
            productCategories.addAll(categories);
        }
    }

    @Override
    public void handleProductList(List<Product> products) {
        // Todo: Insert products into Product TableView.
        // Todo: Rework all the services - Can't really make functions that wont block the UI thread. I need to call new threads on demand.
        new Thread(() -> {
            List<Product> productTableList = this.tblProducts.getItems();
            if (productTableList.isEmpty()) {
                // productTableList.addAll(products);
                for (Product item : products) {
//                Category cat = this.cboProductCategory.getItems().stream().filter(c -> c.getIdCategory() == item.getIdCategory()).findFirst().get();
//                item.setCategory(cat);

                    Category cat = new Category();

                    Category nc = new GetCategoryService(new SQLiteConnection(), cat, item.getIdCategory()).fetchCategory();

                    item.setCategory(nc);
                    productTableList.add(item);
                }
            }
        }).start();

    }
}
