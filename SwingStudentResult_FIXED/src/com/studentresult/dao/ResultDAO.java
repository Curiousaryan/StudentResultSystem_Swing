package com.studentresult.dao;

import com.studentresult.database.DatabaseConnection;
import com.studentresult.model.Result;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ResultDAO {

    private Connection connection;

    public ResultDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection in ResultDAO");
            e.printStackTrace();
        }
    }

    public boolean addResult(Result result) {
        if (isResultExists(result.getStudentId())) {
            System.out.println("Result already exists for student ID: " + result.getStudentId());
            System.out.println("Use updateResult() to modify existing result");
            return false;
        }
        
        String query = "INSERT INTO results (student_id, subject1_marks, subject2_marks, " +
                      "subject3_marks, subject4_marks, subject5_marks, total_marks, " +
                      "percentage, grade, result_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, result.getStudentId());
            pstmt.setDouble(2, result.getANN_marks());
            pstmt.setDouble(3, result.getCNN_marks());
            pstmt.setDouble(4, result.getJAVA_marks());
            pstmt.setDouble(5, result.getFAIML_marks());
            pstmt.setDouble(6, result.getIOT_marks());
            pstmt.setDouble(7, result.getTotalMarks());
            pstmt.setDouble(8, result.getPercentage());
            pstmt.setString(9, result.getGrade());
            pstmt.setString(10, result.getResultStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    result.setResultId(generatedKeys.getInt(1));
                }
                
                System.out.println("Result added successfully for student ID: " + result.getStudentId());
                System.out.println("  Total: " + result.getTotalMarks() + " | Percentage: " + 
                                 String.format("%.2f", result.getPercentage()) + "% | Grade: " + 
                                 result.getGrade() + " | Status: " + result.getResultStatus());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error adding result: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public Result getResultById(int resultId) {
        String query = "SELECT * FROM results WHERE result_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, resultId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractResultFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching result by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public Result getResultByStudentId(int studentId) {
        String query = "SELECT * FROM results WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractResultFromResultSet(rs);
            } else {
                System.out.println("No result found for student ID: " + studentId);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching result by student ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Result> getAllResults() {
        List<Result> results = new ArrayList<>();
        String query = "SELECT * FROM results ORDER BY percentage DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                results.add(extractResultFromResultSet(rs));
            }
            
            System.out.println("Fetched " + results.size() + " results from database");
            
        } catch (SQLException e) {
            System.err.println("Error fetching all results: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }

    public List<Result> getResultsByStatus(String status) {
        List<Result> results = new ArrayList<>();
        String query = "SELECT * FROM results WHERE result_status = ? ORDER BY percentage DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, status.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                results.add(extractResultFromResultSet(rs));
            }
            
            System.out.println("Fetched " + results.size() + " results with status: " + status);
            
        } catch (SQLException e) {
            System.err.println("Error fetching results by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }

    public List<Result> getResultsByGrade(String grade) {
        List<Result> results = new ArrayList<>();
        String query = "SELECT * FROM results WHERE grade = ? ORDER BY percentage DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, grade.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                results.add(extractResultFromResultSet(rs));
            }
            
            System.out.println("Fetched " + results.size() + " results with grade: " + grade);
            
        } catch (SQLException e) {
            System.err.println("Error fetching results by grade: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    public List<Result> getTopResults(int limit) {
        List<Result> results = new ArrayList<>();
        String query = "SELECT * FROM results ORDER BY percentage DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                results.add(extractResultFromResultSet(rs));
            }
            
            System.out.println("Fetched top " + results.size() + " results");
            
        } catch (SQLException e) {
            System.err.println("Error fetching top results: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }

    public List<Result> getResultsByPercentageRange(double minPercentage, double maxPercentage) {
        List<Result> results = new ArrayList<>();
        String query = "SELECT * FROM results WHERE percentage BETWEEN ? AND ? ORDER BY percentage DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setDouble(1, minPercentage);
            pstmt.setDouble(2, maxPercentage);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                results.add(extractResultFromResultSet(rs));
            }
            
            System.out.println("Fetched " + results.size() + " results between " + 
                             minPercentage + "% and " + maxPercentage + "%");
            
        } catch (SQLException e) {
            System.err.println("Error fetching results by percentage range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return results;
    }

    public boolean updateResult(Result result) {
        String query = "UPDATE results SET subject1_marks = ?, subject2_marks = ?, " +
                      "subject3_marks = ?, subject4_marks = ?, subject5_marks = ?, " +
                      "total_marks = ?, percentage = ?, grade = ?, result_status = ? " +
                      "WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setDouble(1, result.getANN_marks());
            pstmt.setDouble(2, result.getCNN_marks());
            pstmt.setDouble(3, result.getJAVA_marks());
            pstmt.setDouble(4, result.getFAIML_marks());
            pstmt.setDouble(5, result.getIOT_marks());
            pstmt.setDouble(6, result.getTotalMarks());
            pstmt.setDouble(7, result.getPercentage());
            pstmt.setString(8, result.getGrade());
            pstmt.setString(9, result.getResultStatus());
            pstmt.setInt(10, result.getStudentId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Result updated successfully for student ID: " + result.getStudentId());
                return true;
            } else {
                System.out.println("No result found for student ID: " + result.getStudentId());
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating result: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteResult(int resultId) {
        String query = "DELETE FROM results WHERE result_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, resultId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Result deleted successfully - ID: " + resultId);
                return true;
            } else {
                System.out.println("No result found with ID: " + resultId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting result: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteResultByStudentId(int studentId) {
        String query = "DELETE FROM results WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Result deleted successfully for student ID: " + studentId);
                return true;
            } else {
                System.out.println("No result found for student ID: " + studentId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting result: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getResultCount() {
        String query = "SELECT COUNT(*) FROM results";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting result count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    public int getPassCount() {
        String query = "SELECT COUNT(*) FROM results WHERE result_status = 'PASS'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting pass count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    public int getFailCount() {
        String query = "SELECT COUNT(*) FROM results WHERE result_status = 'FAIL'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting fail count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    public double getAveragePercentage() {
        String query = "SELECT AVG(percentage) FROM results";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting average percentage: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    

    public double getHighestPercentage() {
        String query = "SELECT MAX(percentage) FROM results";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting highest percentage: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    public double getLowestPercentage() {
        String query = "SELECT MIN(percentage) FROM results";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting lowest percentage: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }

    public double getPassPercentage() {
        int totalCount = getResultCount();
        if (totalCount == 0) return 0.0;
        
        int passCount = getPassCount();
        return (passCount * 100.0) / totalCount;
    }

    public boolean isResultExists(int studentId) {
        String query = "SELECT COUNT(*) FROM results WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking result existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    private Result extractResultFromResultSet(ResultSet rs) throws SQLException {
        return new Result(
            rs.getInt("result_id"),
            rs.getInt("student_id"),
            rs.getDouble("subject1_marks"),
            rs.getDouble("subject2_marks"),
            rs.getDouble("subject3_marks"),
            rs.getDouble("subject4_marks"),
            rs.getDouble("subject5_marks"),
            rs.getDouble("total_marks"),
            rs.getDouble("percentage"),
            rs.getString("grade"),
            rs.getString("result_status"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("ResultDAO connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing ResultDAO connection: " + e.getMessage());
        }
    }
}
