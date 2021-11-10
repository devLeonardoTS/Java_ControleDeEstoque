package com.devldots.inventorymanagement.Interfaces;

import java.util.List;

public interface IDataAccessObject<T> {

    boolean save(T object);

    T get(Object id) throws NullPointerException, IllegalArgumentException;

    List<T> getAll();

    boolean update(T object);

    boolean delete (Object id) throws IllegalArgumentException;

    List<String> getErrorList();

    void setErrorList(List<String> errorList);

}
