package com.leave;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database Connection Manager using JDBC.
 * 
 * In a college viva, explain:
 * 1. JDBC (Java Database Connectivity) is a Java API that manages connecting to a database,
 *    issuing queries, and processing results.
 * 2. We load configuration from `db.properties` so passwords and URLs are not hardcoded in Java code.
 * 3. Class.forName() loads the JDBC driver bytecode dynamically into JVM memory.
 * 4. DriverManager.getConnection() creates an active TCP socket connection to the MySQL server.
 */
public class DBConnection {

    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    private static boolean isInitialized = false;

    // Static initialization block runs once when the class is loaded
    static {
        loadConfiguration();
    }

    /**
     * Loads database credentials from db.properties file located in src/main/resources.
     */
    public static synchronized void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                url = props.getProperty("db.url");
                username = props.getProperty("db.username");
                password = props.getProperty("db.password");
                driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            } else {
                System.err.println("[WARN] db.properties not found on classpath! Using default fallback credentials.");
                url = "jdbc:mysql://localhost:3306/employee_leave_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                username = "root";
                password = "root";
                driver = "com.mysql.cj.jdbc.Driver";
            }

            Class.forName(driver);
            isInitialized = true;
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver class not found: " + driver);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load db.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new database connection.
     * 
     * @return active java.sql.Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (!isInitialized) {
            loadConfiguration();
        }
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("[ERROR] Could not connect to MySQL database at: " + url);
            System.err.println("[HINT] Ensure MySQL service is running and credentials in 'db.properties' are correct.");
            throw e;
        }
    }

    /**
     * Allows tests to override database properties (e.g., using H2 in-memory DB during automated tests).
     */
    public static synchronized void setCustomConnectionDetails(String customUrl, String customUser, String customPass, String customDriver) {
        url = customUrl;
        username = customUser;
        password = customPass;
        driver = customDriver;
        try {
            Class.forName(driver);
            isInitialized = true;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Custom driver class not found: " + customDriver, e);
        }
    }
}
