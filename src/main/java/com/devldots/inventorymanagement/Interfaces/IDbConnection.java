package com.devldots.inventorymanagement.Interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDbConnection {

    public Connection getConnection();

}
