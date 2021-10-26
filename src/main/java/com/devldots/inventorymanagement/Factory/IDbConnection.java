package com.devldots.inventorymanagement.Factory;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDbConnection {

    public Connection getConnection();

}
