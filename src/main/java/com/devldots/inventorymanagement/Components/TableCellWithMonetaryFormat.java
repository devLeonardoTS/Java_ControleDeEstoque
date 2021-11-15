package com.devldots.inventorymanagement.Components;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.devldots.inventorymanagement.Configs.AppConfig;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

public class TableCellWithMonetaryFormat<S, T> extends TableCell<S, T> {

    private final ObjectProperty<DecimalFormat> moneyFormat = new SimpleObjectProperty<>();

    private final BooleanProperty displayWithCurrency = new SimpleBooleanProperty();

    public TableCellWithMonetaryFormat(boolean displayWithCurrency){

        DecimalFormat df = AppConfig.getBrazilMonetaryDecimalFormatter();

        if (displayWithCurrency){
            this.setMoneyFormat(df);
        } else {
            this.setMoneyFormat(df);
            this.getMoneyFormat().setPositivePrefix("");
            this.getMoneyFormat().setNegativePrefix("-");
        }

        this.getMoneyFormat().setMinimumIntegerDigits(1);
        this.getMoneyFormat().setMaximumFractionDigits(2);
    }

    public ObjectProperty<DecimalFormat> moneyFormatProperty(){
        return moneyFormat;
    }

    public BooleanProperty displayWithCurrencyProperty(){
        return displayWithCurrency;
    }

    public final DecimalFormat getMoneyFormat(){
        return moneyFormatProperty().get();
    }

    public final void setMoneyFormat(DecimalFormat moneyFormat){
        moneyFormatProperty().set(moneyFormat);
    }

    public final boolean getDisplayWithCurrency(){
        return displayWithCurrencyProperty().get();
    }

    public final void setDisplayWithCurrency(boolean displayAsCurrency){
        displayWithCurrencyProperty().set(displayAsCurrency);
    }

    @Override
    protected void updateItem(T item, boolean empty){
        super.updateItem(item, empty);
        if (item == null || empty){
            super.setText(null);
        } else {
            try {
                String moneyFormattedItem = this.getMoneyFormat().format(item);

                super.setText(moneyFormattedItem);
                super.setTooltip(new Tooltip(moneyFormattedItem));
            } catch (IllegalArgumentException ex){
                String itemValue = String.valueOf(item);

                super.setText(itemValue);
                super.setTooltip(new Tooltip(itemValue));
            }
        }
    }
}
