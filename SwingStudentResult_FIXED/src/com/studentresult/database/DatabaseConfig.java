package com.studentresult.database;

public class DatabaseConfig {
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DB_URL = "jdbc:mysql://localhost:3306/student_result_system";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "EURU$D762004";
    public static final String DB_PARAMETERS = "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    public static final String COMPLETE_DB_URL = DB_URL + DB_PARAMETERS;
    public static final int CONNECTION_TIMEOUT = 30000;
    private DatabaseConfig() {
        throw new AssertionError("Cannot instantiate DatabaseConfig class");
    }
    
    public static String getCompleteUrl() {
        return COMPLETE_DB_URL;
    }
    
    public static String getConfigInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== Database Configuration ===\n");
        info.append("Driver: ").append(DB_DRIVER).append("\n");
        info.append("URL: ").append(DB_URL).append("\n");
        info.append("Username: ").append(DB_USERNAME).append("\n");
        info.append("Password: ").append("*".repeat(DB_PASSWORD.length())).append("\n");
        info.append("Parameters: ").append(DB_PARAMETERS).append("\n");
        info.append("============================");
        return info.toString();
    }
}