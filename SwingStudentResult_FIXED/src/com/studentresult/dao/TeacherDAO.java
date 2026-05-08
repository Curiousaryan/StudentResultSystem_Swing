package com.studentresult.dao;

import com.studentresult.database.DatabaseConnection;
import com.studentresult.model.Teacher;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TeacherDAO {
	private Connection connection;

    public TeacherDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println(" Failed to establish database connection in TeacherDAO");
            e.printStackTrace();
        }
    }

    public Teacher authenticateTeacher(String username, String password) {
        String query = "SELECT * FROM teachers WHERE username = ? AND password = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Teacher teacher = new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                
                System.out.println("Authentication successful for user: " + username);
                return teacher;
            } else {
                System.out.println("Authentication failed - Invalid credentials");
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM teachers WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error checking username: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean addTeacher(Teacher teacher) {
        if (isUsernameExists(teacher.getUsername())) {
            System.out.println("Username already exists: " + teacher.getUsername());
            return false;
        }
        
        String query = "INSERT INTO teachers (username, password, full_name, email) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, teacher.getUsername());
            pstmt.setString(2, teacher.getPassword());
            pstmt.setString(3, teacher.getFullName());
            pstmt.setString(4, teacher.getEmail());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    teacher.setTeacherId(generatedKeys.getInt(1));
                }
                
                System.out.println("Teacher added successfully: " + teacher.getUsername());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding teacher: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public Teacher getTeacherById(int teacherId) {
        String query = "SELECT * FROM teachers WHERE teacher_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching teacher by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Teacher getTeacherByUsername(String username) {
        String query = "SELECT * FROM teachers WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching teacher by username: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        String query = "SELECT * FROM teachers ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Teacher teacher = new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                teachers.add(teacher);
            }
            
            System.out.println("Fetched " + teachers.size() + " teachers from database");
            
        } catch (SQLException e) {
            System.err.println("Error fetching all teachers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return teachers;
    }

    public boolean updateTeacher(Teacher teacher) {
        String query = "UPDATE teachers SET username = ?, password = ?, full_name = ?, email = ? WHERE teacher_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, teacher.getUsername());
            pstmt.setString(2, teacher.getPassword());
            pstmt.setString(3, teacher.getFullName());
            pstmt.setString(4, teacher.getEmail());
            pstmt.setInt(5, teacher.getTeacherId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Teacher updated successfully: " + teacher.getUsername());
                return true;
            } else {
                System.out.println("No teacher found with ID: " + teacher.getTeacherId());
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating teacher: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int teacherId, String newPassword) {
        String query = "UPDATE teachers SET password = ? WHERE teacher_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, teacherId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Password updated successfully for teacher ID: " + teacherId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean deleteTeacher(int teacherId) {
        String query = "DELETE FROM teachers WHERE teacher_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, teacherId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Teacher deleted successfully - ID: " + teacherId);
                return true;
            } else {
                System.out.println("No teacher found with ID: " + teacherId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting teacher: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getTeacherCount() {
        String query = "SELECT COUNT(*) FROM teachers";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting teacher count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("TeacherDAO connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing TeacherDAO connection: " + e.getMessage());
        }
    }
}
