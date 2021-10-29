package com.devldots.inventorymanagement.Interfaces;

import java.util.Collection;

public interface IDataAccessObject<T> {

    boolean save(T object);

    T get(Object id) throws NullPointerException, IllegalArgumentException;

    Collection<T> getAll();

    T update(T object) throws NullPointerException;

    boolean delete (Object id) throws IllegalArgumentException;

}
