package com.devldots.inventorymanagement.Utils;

import com.devldots.inventorymanagement.Abstracts.AbstractDataEntryValidation;
import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.DataTransferObjects.ProductDTO;
import com.devldots.inventorymanagement.Models.Category;
import com.devldots.inventorymanagement.Models.Product;
import org.apache.commons.io.FilenameUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

public class ProductValidation extends AbstractDataEntryValidation<ProductDTO, Product> {

    public ProductValidation(List<String> errorList) {
        super(errorList);
    }

    public ProductValidation(){
        super(new ArrayList<String>());
    }

    @Override
    public boolean validate(ProductDTO productInput, Product product) {

        if (isAnyRequiredFieldEmpty(productInput)){ return false; }
        if (isAnyStringFieldOutOfRange(productInput)){ return false; }
        if (isAnyMonetaryFieldInvalid(productInput)){ return false; }
        if (isAnyIntegerFieldInvalid(productInput)){ return false; }
        if (isAnyIdFieldInvalid(productInput)){ return false; }

        this.setValidated(parseInputIntoModel(productInput, product));
        if (this.getValidated() == null){
            this.getErrorList().add("Product data couldn't be validated. Please contact the administrator.");
        }

        return this.getErrorList().isEmpty();
    }

    private boolean isAnyRequiredFieldEmpty(ProductDTO productInput){

        String productName = productInput.getName();
        String unitaryPrice = productInput.getUnitaryPrice();
        String quantity = productInput.getQuantity();
        String categoryId = productInput.getCategory().getIdCategory();

        List<String> invalidFieldList = new ArrayList<>();

        requiredFieldValidation("Nome do produto", productName, invalidFieldList);
        requiredFieldValidation("Preço unitário", unitaryPrice, invalidFieldList);
        requiredFieldValidation("Quantidade", quantity, invalidFieldList);
        requiredFieldValidation("Categoria", categoryId, invalidFieldList);

        if (!invalidFieldList.isEmpty()) {
            String errorMessage = "The following fields can't be empty: ";
            for (String invalidField : invalidFieldList) {
                errorMessage += invalidField;
            }
            errorMessage += ".";

            this.getErrorList().add(errorMessage);
            return true;
        }

        return false;

    }

    private boolean isAnyStringFieldOutOfRange(ProductDTO productInput){
        String productName = productInput.getName();

        List<String> invalidFieldMsgList = new ArrayList<>();

        stringLengthValidation("Nome do produto", productName, 1, 100, invalidFieldMsgList);

        if (!invalidFieldMsgList.isEmpty()){
            for (String invalidFieldMsg : invalidFieldMsgList){
                this.getErrorList().add(invalidFieldMsg);
            }
            return true;
        }

        return false;
    }

    private boolean isAnyMonetaryFieldInvalid(ProductDTO productInput){

        List<String> invalidFieldMsgList = new ArrayList<>();

        monetaryValidation("Preço unitário", productInput.getUnitaryPrice(), false, 9, 2, invalidFieldMsgList);

        if (!invalidFieldMsgList.isEmpty()){
            for (String invalidFieldMsg : invalidFieldMsgList){
                this.getErrorList().add(invalidFieldMsg);
            }
            return true;
        }

        return false;

    }

    private boolean isAnyIntegerFieldInvalid(ProductDTO productInput){

        List<String> invalidFieldMsgList = new ArrayList<>();

        integerFieldValidation("Quantidade", productInput.getQuantity(), false, 1, 1000000, invalidFieldMsgList);

        if (!invalidFieldMsgList.isEmpty()){
            for (String invalidFieldMsg : invalidFieldMsgList){
                this.getErrorList().add(invalidFieldMsg);
            }
            return true;
        }

        return false;
    }

    private boolean isAnyIdFieldInvalid(ProductDTO productInput){

        List<String> invalidFieldMsgList = new ArrayList<>();


        try {
            Integer.parseUnsignedInt(productInput.getCategory().getIdCategory());
        } catch (NumberFormatException ex){
            invalidFieldMsgList.add("The selected categoria is invalid.");
        }


        if (!invalidFieldMsgList.isEmpty()){
            for (String invalidFieldMsg : invalidFieldMsgList){
                this.getErrorList().add(invalidFieldMsg);
            }
            return true;
        }

        return false;

    }

    private Product parseInputIntoModel(ProductDTO productInput, Product productModel) throws NullPointerException {
        if (!this.getErrorList().isEmpty()){ return null; }

        Product validProduct = productModel;

        validProduct.setIdCategory(Integer.parseUnsignedInt(productInput.getCategory().getIdCategory()));

        validProduct.setName(productInput.getName());
        validProduct.setUnitaryPrice(parseLocalMonetaryInputToBigDecimal(productInput.getUnitaryPrice()));
        validProduct.setQuantity(Integer.parseUnsignedInt(productInput.getQuantity()));

        boolean isProductWithImage = validProduct.getImageUid() != null && !validProduct.getImageUid().equals(AppConfig.DEFAULT_PRODUCT_IMG_FILE_NAME);
        boolean isAddingFirstProductImage = !isProductWithImage && (productInput.getImagePath() != null && !productInput.getImagePath().isBlank());
        if (isAddingFirstProductImage) {
            String newImageUUID = UUID.randomUUID() + "-" + new Date().getTime() + "." + FilenameUtils.getExtension(productInput.getImagePath());
            validProduct.setImageUid(newImageUUID);
        }

        Category validProductCategory = new Category();

        validProductCategory.setIdCategory(Integer.parseUnsignedInt(productInput.getCategory().getIdCategory()));
        validProductCategory.setName(productInput.getCategory().getName());

        validProduct.setCategory(validProductCategory);

        return validProduct;

    }

    private void requiredFieldValidation(String fieldName, String fieldValue, List<String> invalidFieldList){

        if (fieldValue == null){

            try {
                if (invalidFieldList.isEmpty()) {
                    invalidFieldList.add(fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
                } else {
                    invalidFieldList.add(", " + fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1));
                }
            } catch (IndexOutOfBoundsException ex){
                return;
            }

            return;
        }

        if (fieldValue.isBlank()){
            try {
                if (invalidFieldList.isEmpty()) {
                    invalidFieldList.add(fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
                } else {
                    invalidFieldList.add(", " + fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1));
                }
            } catch (IndexOutOfBoundsException ex){
                return;
            }
        }

        return;

    }

    private void stringLengthValidation(String fieldName, String fieldValue, int min, int max, List<String> invalidFieldMsgList){

        if (fieldValue.length() < min || fieldValue.length() > max){
            invalidFieldMsgList.add("\"" + fieldName + "\" needs to be between " + min + " and " + max + " characters.");
        }

    }

    private void integerFieldValidation(String fieldName, String fieldValue, boolean allowNegative, List<String> invalidFieldMsgList){
        if (allowNegative){
            try {
                Integer.parseInt(fieldValue);
            } catch (NumberFormatException ex){
                invalidFieldMsgList.add("" + fieldName + "'s value must be an integer number.");
            }
        }

        if (!allowNegative){
            try {
                Integer.parseUnsignedInt(fieldValue);
            } catch (NumberFormatException ex){
                invalidFieldMsgList.add(fieldName + "'s value must be a positive integer number.");
            }
        }
    }

    private void integerFieldValidation(String fieldName, String fieldValue, boolean allowNegative, int min, int max, List<String> invalidFieldMsgList){

        if (allowNegative){
            try {
                int value = Integer.parseInt(fieldValue);
                if (value < min || value > max) {
                    invalidFieldMsgList.add(fieldName + "'s value needs to be an integer number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException ex){
                invalidFieldMsgList.add(fieldName + "'s value must be an integer number.");
            }
        }

        if (!allowNegative){
            try {
                int value = Integer.parseUnsignedInt(fieldValue);
                if (value < min || value > max) {
                    invalidFieldMsgList.add(fieldName + "'s value needs to be a positive integer number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException ex){
                invalidFieldMsgList.add(fieldName + "'s value must be a positive integer number.");
            }
        }
    }

    private void monetaryValidation(String fieldName, String fieldValue, boolean allowNegative, int maxPrecision, int maxScale, List<String> invalidFieldMsgList){

        if (maxPrecision < maxScale) {
            invalidFieldMsgList.add(fieldName + "'s max precision (" + maxPrecision + ") can't be lower than max scale ("+ maxScale +").");
            return;
        }

        if (fieldValue.matches("(.*)([^0-9,.+-])(.*)")){
            invalidFieldMsgList.add(fieldName + "'s input don't accept literal characters.");
            return;
        }

        if (fieldValue.matches("(.*)([.]{2,}|[,]{2,}|[.],|,[.]|\\s+)(.*)")){
            invalidFieldMsgList.add(fieldName + "'s input doesn't look right, check for typos or white spaces.");
            return;
        }

        DecimalFormatSymbols localeDecimalFormatSymbols = DecimalFormatSymbols.getInstance();

        String decimalSeparator = Character.toString(localeDecimalFormatSymbols.getDecimalSeparator());

        boolean valueHasDecimalPart = fieldValue.contains(decimalSeparator);

        if (!allowNegative && fieldValue.contains("-")){
            invalidFieldMsgList.add(fieldName + " needs to be a positive value.");
            return;
        }

        if (valueHasDecimalPart) {
            try {

                String decimalPart = (String) fieldValue.subSequence(fieldValue.lastIndexOf(decimalSeparator) + 1, fieldValue.length());
                boolean decimalPartBeyondMaxScale = decimalPart.length() > maxScale;
                if (decimalPartBeyondMaxScale) {
                    invalidFieldMsgList.add(fieldName + " has more decimal values than it should. Max scale is (" + maxScale + ").");
                    return;
                }

            } catch (IndexOutOfBoundsException ex){
                invalidFieldMsgList.add(fieldName + " couldn't be verified, please contact the administrator with the following message: " + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                return;
            }
        }

        String thousandSeparator = Character.toString(localeDecimalFormatSymbols.getGroupingSeparator());

        if (fieldValue.contains(thousandSeparator)){

            String integerPart = valueHasDecimalPart ? fieldValue.split("[" + decimalSeparator + "]")[0] : fieldValue;
            String[] integerGroup = integerPart.split("[" + thousandSeparator + "]");

            for (int i = 0; i < integerGroup.length; i++){
                boolean isInputOverflowingIntegerGroup = i > 0 && integerGroup[i].length() != 3;
                if (isInputOverflowingIntegerGroup){
                    invalidFieldMsgList.add(fieldName + "'s input doesn't look right, check if your thousand separators are being overflown.");
                    return;
                }
            }

        }

        try {

            DecimalFormat decimalFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
            decimalFormatter.setParseBigDecimal(true);

            BigDecimal fieldValueAsBigDecimal = (BigDecimal) decimalFormatter.parseObject(fieldValue);

            if (fieldValueAsBigDecimal.precision() > maxPrecision){
                invalidFieldMsgList.add(fieldName + "'s precision (" + fieldValueAsBigDecimal.precision() + ") is beyond desired precision (" + maxPrecision + "), please contact the administrator.");
                return;
            }

        } catch (ParseException ex) {
            invalidFieldMsgList.add(fieldName + " couldn't be verified, please contact the administrator with the following message: " + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            return;
        }

        return;

    }

    private BigDecimal parseLocalMonetaryInputToBigDecimal(String monetaryInput){

        try {
            DecimalFormat decimalFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
            decimalFormatter.setParseBigDecimal(true);

            return (BigDecimal) decimalFormatter.parseObject(monetaryInput);
        } catch (ParseException ex) {
            return null;
        }

    }
}
