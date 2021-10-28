package com.devldots.inventorymanagement.Components;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

public class TableCellWithMonetaryFormat<S, T> extends TableCell<S, T> {
    private final ObjectProperty<Locale> locale = new SimpleObjectProperty();

    private final ObjectProperty<DecimalFormat> moneyFormat = new SimpleObjectProperty();

    private final StringProperty monetarySymbol = new SimpleStringProperty();

    private final BooleanProperty displayWithCurrency = new SimpleBooleanProperty();

    public TableCellWithMonetaryFormat(Locale locale, boolean displayWithCurrency){
        this.setLocale(locale);

        if (displayWithCurrency){
            this.setMoneyFormat((DecimalFormat) NumberFormat.getCurrencyInstance(this.getLocale()));
            this.setMonetarySymbol(this.getMoneyFormat().getCurrency().getSymbol(this.getLocale()));

            this.getMoneyFormat().setNegativePrefix(this.getMonetarySymbol() + " -");
        } else {
            this.setMoneyFormat((DecimalFormat) NumberFormat.getCurrencyInstance(this.getLocale()));
            this.getMoneyFormat().setPositivePrefix("");
            this.getMoneyFormat().setNegativePrefix("-");
        }

        this.getMoneyFormat().setMinimumIntegerDigits(1);
        this.getMoneyFormat().setMaximumFractionDigits(2);
    }

    public ObjectProperty<Locale> localeProperty(){
        return locale;
    }

    public ObjectProperty<DecimalFormat> moneyFormatProperty(){
        return moneyFormat;
    }

    public StringProperty monetarySymbolProperty(){
        return monetarySymbol;
    }

    public BooleanProperty displayWithCurrencyProperty(){
        return displayWithCurrency;
    }

    public final Locale getLocale(){
        return localeProperty().get();
    }

    public final void setLocale(Locale locale) {
        localeProperty().set(locale);
    }

    public final DecimalFormat getMoneyFormat(){
        return moneyFormatProperty().get();
    }

    public final void setMoneyFormat(DecimalFormat moneyFormat){
        moneyFormatProperty().set(moneyFormat);
    }

    public final String getMonetarySymbol(){
        return monetarySymbolProperty().get();
    }

    public final void setMonetarySymbol(String simbol){
        monetarySymbolProperty().set(simbol);
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
