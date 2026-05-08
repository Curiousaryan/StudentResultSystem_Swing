package com.studentresult.dao;

import com.studentresult.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityLogDAO {

    private Connection connection;

    public ActivityLogDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection in ActivityLogDAO");
            e.printStackTrace();
        }
    }

    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String ADD_STUDENT = "ADD_STUDENT";
    public static final String UPDATE_STUDENT = "UPDATE_STUDENT";
    public static final String DELETE_STUDENT = "DELETE_STUDENT";
    public static final String VIEW_STUDENT = "VIEW_STUDENT";
    public static final String ADD_RESULT = "ADD_RESULT";
    public static final String UPDATE_RESULT = "UPDATE_RESULT";
    public static final String DELETE_RESULT = "DELETE_RESULT";
    public static final String VIEW_RESULT = "VIEW_RESULT";
    public static final String FILE_BACKUP = "FILE_BACKUP";
    public static final String FILE_RESTORE = "FILE_RESTORE";
    public static final String EXPORT_PDF = "EXPORT_PDF";
    public static final String SYSTEM_START = "SYSTEM_START";
    public static final String SYSTEM_SHUTDOWN = "SYSTEM_SHUTDOWN";
    public static final String ERROR = "ERROR";

    public synchronized boolean logActivity(String activityType, String description, String performedBy) {
        String query = "INSERT INTO activity_log (activity_type, description, performed_by) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, activityType);
            pstmt.setString(2, description);
            pstmt.setString(3, performedBy);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                int logId = 0;
                if (generatedKeys.next()) {
                    logId = generatedKeys.getInt(1);
                }
                
                System.out.println("✓ Activity logged [ID: " + logId + "] - " + activityType + 
                                 " by " + performedBy);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error logging activity: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean logActivity(String activityType, String description) {
        return logActivity(activityType, description, "SYSTEM");
    }

    public List<Map<String, Object>> getAllLogs() {
        List<Map<String, Object>> logs = new ArrayList<>();
        String query = "SELECT * FROM activity_log ORDER BY log_timestamp DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("log_id", rs.getInt("log_id"));
                log.put("activity_type", rs.getString("activity_type"));
                log.put("description", rs.getString("description"));
                log.put("performed_by", rs.getString("performed_by"));
                log.put("log_timestamp", rs.getTimestamp("log_timestamp").toLocalDateTime());
                logs.add(log);
            }
            
            System.out.println("Fetched " + logs.size() + " activity logs");
            
        } catch (SQLException e) {
            System.err.println("Error fetching all logs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }

    public List<Map<String, Object>> getLogsByType(String activityType) {
        List<Map<String, Object>> logs = new ArrayList<>();
        String query = "SELECT * FROM activity_log WHERE activity_type = ? ORDER BY log_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, activityType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("log_id", rs.getInt("log_id"));
                log.put("activity_type", rs.getString("activity_type"));
                log.put("description", rs.getString("description"));
                log.put("performed_by", rs.getString("performed_by"));
                log.put("log_timestamp", rs.getTimestamp("log_timestamp").toLocalDateTime());
                logs.add(log);
            }
            
            System.out.println("Fetched " + logs.size() + " logs for type: " + activityType);
            
        } catch (SQLException e) {
            System.err.println("Error fetching logs by type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }
    
    public List<Map<String, Object>> getLogsByUser(String performedBy) {
        List<Map<String, Object>> logs = new ArrayList<>();
        String query = "SELECT * FROM activity_log WHERE performed_by = ? ORDER BY log_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, performedBy);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("log_id", rs.getInt("log_id"));
                log.put("activity_type", rs.getString("activity_type"));
                log.put("description", rs.getString("description"));
                log.put("performed_by", rs.getString("performed_by"));
                log.put("log_timestamp", rs.getTimestamp("log_timestamp").toLocalDateTime());
                logs.add(log);
            }
            
            System.out.println("Fetched " + logs.size() + " logs for user: " + performedBy);
            
        } catch (SQLException e) {
            System.err.println("Error fetching logs by user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }

    public List<Map<String, Object>> getRecentLogs(int limit) {
        List<Map<String, Object>> logs = new ArrayList<>();
        String query = "SELECT * FROM activity_log ORDER BY log_timestamp DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("log_id", rs.getInt("log_id"));
                log.put("activity_type", rs.getString("activity_type"));
                log.put("description", rs.getString("description"));
                log.put("performed_by", rs.getString("performed_by"));
                log.put("log_timestamp", rs.getTimestamp("log_timestamp").toLocalDateTime());
                logs.add(log);
            }
            
            System.out.println("Fetched " + logs.size() + " recent logs");
            
        } catch (SQLException e) {
            System.err.println("Error fetching recent logs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }

    public List<Map<String, Object>> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> logs = new ArrayList<>();
        String query = "SELECT * FROM activity_log WHERE log_timestamp BETWEEN ? AND ? ORDER BY log_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(startDate));
            pstmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("log_id", rs.getInt("log_id"));
                log.put("activity_type", rs.getString("activity_type"));
                log.put("description", rs.getString("description"));
                log.put("performed_by", rs.getString("performed_by"));
                log.put("log_timestamp", rs.getTimestamp("log_timestamp").toLocalDateTime());
                logs.add(log);
            }
            
            System.out.println("✓ Fetched " + logs.size() + " logs between dates");
            
        } catch (SQLException e) {
            System.err.println("Error fetching logs by date range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }

    public List<Map<String, Object>> searchLogs(String keyword) {
        List<Map<String, Object>> logs = new ArrayList<>();
        String query = "SELECT * FROM activity_log WHERE description LIKE ? ORDER BY log_timestamp DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("log_id", rs.getInt("log_id"));
                log.put("activity_type", rs.getString("activity_type"));
                log.put("description", rs.getString("description"));
                log.put("performed_by", rs.getString("performed_by"));
                log.put("log_timestamp", rs.getTimestamp("log_timestamp").toLocalDateTime());
                logs.add(log);
            }
            
            System.out.println("Found " + logs.size() + " logs matching keyword: " + keyword);
            
        } catch (SQLException e) {
            System.err.println("Error searching logs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logs;
    }

    public int deleteOldLogs(int daysOld) {
        String query = "DELETE FROM activity_log WHERE log_timestamp < DATE_SUB(NOW(), INTERVAL ? DAY)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, daysOld);
            int rowsDeleted = pstmt.executeUpdate();
            
            System.out.println("Deleted " + rowsDeleted + " logs older than " + daysOld + " days");
            return rowsDeleted;
            
        } catch (SQLException e) {
            System.err.println("Error deleting old logs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    public boolean clearAllLogs() {
        String query = "TRUNCATE TABLE activity_log";
        
        try (Statement stmt = connection.createStatement()) {
            
            stmt.executeUpdate(query);
            System.out.println("All activity logs cleared");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error clearing logs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public int getLogCount() {
        String query = "SELECT COUNT(*) FROM activity_log";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting log count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    public int getLogCountByType(String activityType) {
        String query = "SELECT COUNT(*) FROM activity_log WHERE activity_type = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, activityType);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting log count by type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    public Map<String, Integer> getActivitySummary() {
        Map<String, Integer> summary = new HashMap<>();
        String query = "SELECT activity_type, COUNT(*) as count FROM activity_log GROUP BY activity_type";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                summary.put(rs.getString("activity_type"), rs.getInt("count"));
            }
            
            System.out.println("Generated activity summary with " + summary.size() + " activity types");
            
        } catch (SQLException e) {
            System.err.println("Error generating activity summary: " + e.getMessage());
            e.printStackTrace();
        }
        
        return summary;
    }

    public void displayLogs(List<Map<String, Object>> logs) {
        if (logs.isEmpty()) {
            System.out.println("\nNo activity logs found.");
            return;
        }
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                           ACTIVITY LOGS                                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-4s │ %-15s │ %-30s │ %-12s │ %-19s ║%n", 
                         "ID", "Type", "Description", "Performed By", "Timestamp");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣");
        
        for (Map<String, Object> log : logs) {
            String description = log.get("description").toString();
            if (description.length() > 30) {
                description = description.substring(0, 27) + "...";
            }
            
            System.out.printf("║ %-4d │ %-15s │ %-30s │ %-12s │ %-19s ║%n",
                log.get("log_id"),
                log.get("activity_type"),
                description,
                log.get("performed_by"),
                log.get("log_timestamp")
            );
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("Total logs: " + logs.size());
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("ActivityLogDAO connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing ActivityLogDAO connection: " + e.getMessage());
        }
    }
}