package org.projeto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private String URL = "jdbc:mysql://127.0.0.1:3306/projetoa3";
    private String USER = "root";
    private static final String PASSWORD = "sua_senha_aqui"; //substitua pela sua senha

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
