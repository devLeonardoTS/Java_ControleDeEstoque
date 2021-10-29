package com.devldots.inventorymanagement.Components;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

public class TableCellWithDateFormat<S, T> extends TableCell<S, T> {
    private final StringProperty dateTimeFormatPattern = new SimpleStringProperty();

    public TableCellWithDateFormat(String dateTimeFormatPattern){
        setDateTimeFormatPattern(dateTimeFormatPattern);
    }

    public StringProperty dateTimeFormatPatternProperty(){
        return dateTimeFormatPattern;
    }

    public final String getDateTimeFormatPattern(){
        return dateTimeFormatPatternProperty().get();
    }

    public final void setDateTimeFormatPattern(String dateTimeFormatPattern) {
        dateTimeFormatPatternProperty().set(dateTimeFormatPattern);
    }

    @Override
    protected void updateItem(T item, boolean empty){
        super.updateItem(item, empty);
        if (empty){

            super.setText(null);

        } else {

            LocalDateTime itemAsLDT = null;
            if (item instanceof LocalDateTime){
                itemAsLDT = (LocalDateTime) item;

                if (itemAsLDT != null){
                    String dateFormattedItem = String.format(itemAsLDT.format(DateTimeFormatter.ofPattern(getDateTimeFormatPattern())));
                    super.setText(dateFormattedItem);
                    super.setTooltip(new Tooltip(dateFormattedItem));
                    return;
                }
            }

            if (item != null){
                String itemValue = String.valueOf(item);
                super.setText(itemValue);
                super.setTooltip(new Tooltip(itemValue));
            }

        }
    }
}