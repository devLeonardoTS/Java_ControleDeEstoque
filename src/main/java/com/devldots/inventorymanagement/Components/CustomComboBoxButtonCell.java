package com.devldots.inventorymanagement.Components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ListCell;

public class CustomComboBoxButtonCell<T> extends ListCell<T> {

    // Named subclass of ListCell.
    private final StringProperty promptText = new SimpleStringProperty();

    public CustomComboBoxButtonCell(String promptText){
        this.promptText.addListener((obs, oldText, newText) -> {
            if (super.isEmpty() || super.getItem() == null){
                super.setText(newText);
            }
        });
        setPromptText(promptText);
    }

    public StringProperty promptTextProperty(){
        return promptText;
    }

    public final String getPromptText(){
        return promptTextProperty().get();
    }

    public final void setPromptText(String promptText) {
        promptTextProperty().set(promptText);
    }

    @Override
    protected void updateItem(T item, boolean empty){
        super.updateItem(item, empty);
        if (item == null || empty){
            super.setText(getPromptText());
        } else {
            super.setText(item.toString());
        }
    }

}
