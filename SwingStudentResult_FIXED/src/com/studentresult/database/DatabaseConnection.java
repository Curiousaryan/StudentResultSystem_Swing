package com.studentresult.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private DatabaseConnection() {
        try {
            Class.forName(DatabaseConfig.DB_DRIVER);
            System.out.println(" MySQL JDBC Driver loaded successfully");
            
            // Establish connection
            this.connection = DriverManager.getConnection(
                DatabaseConfig.COMPLETE_DB_URL,
                DatabaseConfig.DB_USERNAME,
                DatabaseConfig.DB_PASSWORD
            );
            
            System.out.println(" Database connection established successfully");
            System.out.println(" Connected to: " + DatabaseConfig.DB_URL);
            
        } catch (ClassNotFoundException e) {
            System.err.println(" MySQL JDBC Driver not found!");
            System.err.println(" Make sure mysql-connector-java.jar is in classpath");
            e.printStackTrace();
            
        } catch (SQLException e) {
            System.err.println(" Failed to establish database connection!");
            System.err.println(" Please check:");
            System.err.println("  1. MySQL server is running");
            System.err.println("  2. Database 'student_result_system' exists");
            System.err.println("  3. Username and password are correct");
            System.err.println("  4. MySQL is running on port 3306");
            e.printStackTrace();
        }
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        // Check if connection is closed or null
        if (connection == null || connection.isClosed()) {
            System.out.println(" Connection is closed. Reconnecting...");
            reconnect();
        }
        return connection;
    }
    
    private void reconnect() throws SQLException {
        try {
            this.connection = DriverManager.getConnection(
                DatabaseConfig.COMPLETE_DB_URL,
                DatabaseConfig.DB_USERNAME,
                DatabaseConfig.DB_PASSWORD
            );
            System.out.println(" Database reconnected successfully");
            
        } catch (SQLException e) {
            System.err.println(" Reconnection failed!");
            throw e;
        }
    }
    
    public boolean testConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                // Test with a simple query
                return connection.isValid(5); // 5 second timeout
            }
        } catch (SQLException e) {
            System.err.println(" Connection test failed: " + e.getMessage());
        }
        return false;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Database connection closed successfully");
            }
        } catch (SQLException e) {
            System.err.println(" Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String getConnectionStatus() {
        try {
            if (connection != null && !connection.isClosed()) {
                return " Connected to: " + connection.getMetaData().getURL();
            } else {
                return " Connection is closed";
            }
        } catch (SQLException e) {
            return " Error checking connection: " + e.getMessage();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("DATABASE CONNECTION TEST");
        System.out.println("========================================\n");
        
        // Display configuration
        System.out.println(DatabaseConfig.getConfigInfo());
        System.out.println();
        
        // Test connection
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        
        try {
            Connection conn = dbConn.getConnection();
            
            if (dbConn.testConnection()) {
                System.out.println("\n CONNECTION TEST SUCCESSFUL ");
                System.out.println(dbConn.getConnectionStatus());
                
                // Display database metadata
                System.out.println("\n=== Database Information ===");
                System.out.println("Database Product: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Database Version: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("Driver Name: " + conn.getMetaData().getDriverName());
                System.out.println("Driver Version: " + conn.getMetaData().getDriverVersion());
                
            } else {
                System.out.println("\n CONNECTION TEST FAILED ");
            }
            
        } catch (SQLException e) {
            System.out.println("\n CONNECTION TEST FAILED ");
            e.printStackTrace();
        } finally {
            dbConn.closeConnection();
        }
        
        System.out.println("\n========================================");
        System.out.println("TEST COMPLETED");
        System.out.println("========================================");
    }
}