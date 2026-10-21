package com.scc.companyitassets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Paths;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL =
            "jdbc:sqlite:" +
                    Paths.get("CompanyITassetsDatabase.db")
                            .toAbsolutePath()
                            .normalize();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() throws SQLException {

        String sql = "CREATE TABLE IF NOT EXISTS assets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "device_name TEXT NOT NULL, " +
                "assigned_to TEXT NOT NULL, " +
                "serial_no TEXT NOT NULL UNIQUE, " +
                "\"condition\" TEXT NOT NULL " +
                "CHECK (\"condition\" IN " +
                "('NEW', 'GOOD', 'REPAIR_NEEDED'))" +
                ")";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
        }
    }
}