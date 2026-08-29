package com.paymentagent.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL =
            System.getenv().getOrDefault(
                    "DATABASE_URL",
                    "jdbc:postgresql://localhost:5432/payment_agent"
            );

    private static final String USER =
            System.getenv().getOrDefault(
                    "DATABASE_USER",
                    "payment_user"
            );

    private static final String PASSWORD =
            System.getenv("DATABASE_PASSWORD");

    public static Connection getConnection()
            throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new IllegalStateException(
                    "DATABASE_PASSWORD environment variable is not set"
            );
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}