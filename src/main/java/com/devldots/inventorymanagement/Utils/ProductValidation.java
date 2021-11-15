package com.devldots.inventorymanagement.Utils;

import com.devldots.inventorymanagement.Abstracts.AbstractDataEntryValidation;
import com.devldots.inventorymanagement.Configs.AppConfig;
import com.devldots.inventorymanagement.DataTransferObjects.ProductDTO;
import com.devldots.inventorymanagement.Models.Category;
import com.devldots.inventorymanagement.Models.Product;
import org.apache.commons.io.FilenameUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
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
            String errorMessage = "Os seguintes campos devem ser preenchidos: ";
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

        monetaryValidation("Preço unitário", productInput.getUnitaryPrice(), false, false, 9, 2, invalidFieldMsgList);

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
            invalidFieldMsgList.add("A categoria selecionada é inválida.");
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

        validProduct.setName(productInput.getName().trim());
        validProduct.setUnitaryPrice(parseLocalMonetaryInputToBigDecimal(productInput.getUnitaryPrice().trim()));
        validProduct.setQuantity(Integer.parseUnsignedInt(productInput.getQuantity().trim()));

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
            invalidFieldMsgList.add("\"" + fieldName + "\" deve possuir entre " + min + " e " + max + " caracteres.");
        }

    }

    private void integerFieldValidation(String fieldName, String fieldValue, boolean allowNegative, List<String> invalidFieldMsgList){
        if (allowNegative){
            try {
                Integer.parseInt(fieldValue);
            } catch (NumberFormatException ex){
                invalidFieldMsgList.add("O valor de " + fieldName + " deve ser um número inteiro.");
            }
        }

        if (!allowNegative){
            try {
                Integer.parseUnsignedInt(fieldValue);
            } catch (NumberFormatException ex){
                invalidFieldMsgList.add("O valor de " + fieldName + " deve ser um número inteiro positivo.");
            }
        }
    }

    private void integerFieldValidation(String fieldName, String fieldValue, boolean allowNegative, int min, int max, List<String> invalidFieldMsgList){

        if (allowNegative){
            try {
                int value = Integer.parseInt(fieldValue);
                if (value < min || value > max) {
                    invalidFieldMsgList.add("O valor de " + fieldName + " deve ser um número inteiro de no mínimo " + min + " e no máximo " + max + ".");
                }
            } catch (NumberFormatException ex){
                invalidFieldMsgList.add("O valor de " + fieldName + " deve ser um número inteiro.");
            }
        }

        if (!allowNegative){
            try {
                int value = Integer.parseUnsignedInt(fieldValue);
                if (value < min || value > max) {
                    invalidFieldMsgList.add("O valor de " + fieldName + " deve ser um número inteiro positivo de no mínimo " + min + " e no máximo " + max + ".");
                }
            } catch (NumberFormatException ex){
                invalidFieldMsgList.add("O valor de " + fieldName + " deve ser um número inteiro positivo.");
            }
        }
    }

    private void monetaryValidation(String fieldName, String fieldValue, boolean allowNegative, boolean allowZero, int maxPrecision, int maxScale, List<String> invalidFieldMsgList){

        if (maxPrecision < maxScale) {
            invalidFieldMsgList.add("A precisão máxima (" + maxPrecision + ") para o campo " + fieldName + " não pode ser menor que a escala máxima definida (" + maxScale + ").");
            return;
        }

        if (fieldValue.matches("(.*)([^0-9,.+-])(.*)")){
            invalidFieldMsgList.add("O campo " + fieldName + " não aceita caracteres literais.");
            return;
        }

        if (fieldValue.matches("(.*)([.]{2,}|[,]{2,}|[.],|,[.]|\\s+)(.*)")){
            invalidFieldMsgList.add(fieldName + " parece conter valores inesperados, verifique por erros de digitação ou espaços em branco.");
            return;
        }

        String decimalSeparator = ",";

        boolean valueHasDecimalPart = fieldValue.contains(decimalSeparator);

        if (!allowNegative && fieldValue.contains("-")){
            invalidFieldMsgList.add(fieldName + " precisa conter um valor positivo.");
            return;
        }

        if (valueHasDecimalPart) {
            try {

                String decimalPart = (String) fieldValue.subSequence(fieldValue.lastIndexOf(decimalSeparator) + 1, fieldValue.length());
                boolean decimalPartBeyondMaxScale = decimalPart.length() > maxScale;
                if (decimalPartBeyondMaxScale) {
                    invalidFieldMsgList.add(fieldName + " possui mais números na parte decimal do que deveria. A escala máxima é (" + maxScale + ").");
                    return;
                }

            } catch (IndexOutOfBoundsException ex){
                invalidFieldMsgList.add(fieldName + " não pôde ser verificado, por favor entre em contato com o administrador com a seguinte mensagem: " + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                return;
            }
        }

        String thousandSeparator = ".";

        if (fieldValue.contains(thousandSeparator)){

            String integerPart = valueHasDecimalPart ? fieldValue.split("[" + decimalSeparator + "]")[0] : fieldValue;
            String[] integerGroup = integerPart.split("[" + thousandSeparator + "]");

            for (int i = 0; i < integerGroup.length; i++){
                boolean isInputOverflowingIntegerGroup = i > 0 && integerGroup[i].length() != 3;
                if (isInputOverflowingIntegerGroup){
                    invalidFieldMsgList.add(fieldName + " parece conter valores inesperados, verifique se os números estão sendo separados em milhares e a separação está correta (Ex: 1.000.000,00).");
                    return;
                }
            }

        }

        try {

            DecimalFormat df = AppConfig.getBrazilMonetaryDecimalFormatter();

            BigDecimal fieldValueAsBigDecimal = (BigDecimal) df.parseObject(fieldValue);

            if (fieldValueAsBigDecimal.precision() > maxPrecision){
                invalidFieldMsgList.add("O valor de " + fieldName + " deve conter no máximo " + maxPrecision + " dígitos ao total.");
                return;
            }

            if (!allowZero && fieldValueAsBigDecimal.doubleValue() == 0) {
                invalidFieldMsgList.add("O valor de " + fieldName + " não pode ser zero.");
            }

        } catch (ParseException ex) {
            invalidFieldMsgList.add(fieldName + " não pôde ser verificado, por favor entre em contato com o administrador com a seguinte mensagem: " + this.getClass().getSimpleName() + " - " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            return;
        }

        return;

    }

    private BigDecimal parseLocalMonetaryInputToBigDecimal(String monetaryInput){

        try {
            DecimalFormat decimalFormatter = AppConfig.getBrazilMonetaryDecimalFormatter();

            return (BigDecimal) decimalFormatter.parseObject(monetaryInput);
        } catch (ParseException ex) {
            return null;
        }

    }

}
