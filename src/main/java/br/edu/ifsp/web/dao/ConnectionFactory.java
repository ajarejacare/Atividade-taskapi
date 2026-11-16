package br.edu.ifsp.web.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL =
            "jdbc:mysql://localhost:3306/taskdb";

    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL não encontrado.", e);
        }
        String usuario = valorAmbiente("DB_USER", DEFAULT_USER);
        String senha = valorAmbiente("DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(URL, usuario, senha);
    }

    private String valorAmbiente(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}