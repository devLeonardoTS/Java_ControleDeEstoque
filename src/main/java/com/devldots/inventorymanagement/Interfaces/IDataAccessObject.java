package com.devldots.inventorymanagement.Interfaces;

import java.util.List;

public interface IDataAccessObject<T> {

    boolean save(T object);

    T get(Object id) throws NullPointerException, IllegalArgumentException;

    List<T> getAll();

    T update(T object) throws NullPointerException;

    boolean delete (Object id) throws IllegalArgumentException;

}
