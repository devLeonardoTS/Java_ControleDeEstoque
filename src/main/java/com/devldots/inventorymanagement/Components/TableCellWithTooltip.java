package com.devldots.inventorymanagement.Components;

import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

public class TableCellWithTooltip<S, T> extends TableCell<S, T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null | empty){
            super.setText(null);
            return;
        }

        String itemValue = String.valueOf(item);

        super.setText(itemValue);
        super.setTooltip(new Tooltip(itemValue));
    }
}
