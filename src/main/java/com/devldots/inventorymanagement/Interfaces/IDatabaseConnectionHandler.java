package com.devldots.inventorymanagement.Interfaces;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDatabaseConnectionHandler {

    public Connection getConnection();

}
