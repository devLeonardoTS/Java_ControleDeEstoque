package com.devldots.inventorymanagement.Abstracts;

import java.util.List;

public abstract class AbstractDataEntryValidation<TAnyDTO, SAnyModel> {

    private SAnyModel validatedObject;
    private List<String> errorList;

    public AbstractDataEntryValidation(List<String> errorList){
        setErrorList(errorList);
    }

    public abstract boolean validate(TAnyDTO userInputObject, SAnyModel modelObject);

    public SAnyModel getValidated() {
        if (!this.getErrorList().isEmpty()){ return null; }
        return this.validatedObject;
    }

    public void setValidated(SAnyModel validatedObject) {
        this.validatedObject = validatedObject;
    }

    public List<String> getErrorList() {
        return this.errorList;
    }

    public void setErrorList(List<String> errorList) {
        this.errorList = errorList;
    }

    public String getLastError() {
        if (this.getErrorList().isEmpty()){ return ""; }
        return errorList.get(errorList.size() - 1);
    }

    public String getFirstError() {
        if (this.getErrorList().isEmpty()){ return ""; }
        return errorList.get(0);
    }

}
