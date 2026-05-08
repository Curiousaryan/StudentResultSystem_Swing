package com.studentresult.dao;

import com.studentresult.database.DatabaseConnection;
import com.studentresult.model.Student;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class StudentDAO {
    private Connection connection;

    public StudentDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection in StudentDAO");
            e.printStackTrace();
        }
    }

    public Student authenticateStudent(String rollNumber, String motherName) {
        String query = "SELECT * FROM students WHERE roll_number = ? AND mother_name = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, rollNumber);
            pstmt.setString(2, motherName);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Student student = new Student(
                    rs.getInt("student_id"),
                    rs.getString("roll_number"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("mother_name"),
                    rs.getInt("added_by"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                
                System.out.println("Student authentication successful: " + rollNumber);
                return student;
            } else {
                System.out.println("Authentication failed - Invalid roll number or mother's name");
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error during student authentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean addStudent(Student student) {

        if (isRollNumberExists(student.getRollNumber())) {
            System.out.println("Roll number already exists: " + student.getRollNumber());
            return false;
        }
        
        String query = "INSERT INTO students (roll_number, student_name, email, mother_name, added_by) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, student.getRollNumber());
            pstmt.setString(2, student.getStudentName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMotherName());
            pstmt.setInt(5, student.getAddedBy());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    student.setStudentId(generatedKeys.getInt(1));
                }
                
                System.out.println("Student added successfully: " + student.getRollNumber());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public int addMultipleStudents(List<Student> students) {
        int successCount = 0;
        
        for (Student student : students) {
            if (addStudent(student)) {
                successCount++;
            }
        }
        
        System.out.println("Bulk insert completed: " + successCount + "/" + students.size() + " students added");
        return successCount;
    }
 public Student getStudentById(int studentId) {
        String query = "SELECT * FROM students WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Student(
                    rs.getInt("student_id"),
                    rs.getString("roll_number"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("mother_name"),
                    rs.getInt("added_by"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching student by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public Student getStudentByRollNumber(String rollNumber) {
        String query = "SELECT * FROM students WHERE roll_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, rollNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Student(
                    rs.getInt("student_id"),
                    rs.getString("roll_number"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("mother_name"),
                    rs.getInt("added_by"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching student by roll number: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("student_id"),
                    rs.getString("roll_number"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("mother_name"),
                    rs.getInt("added_by"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                students.add(student);
            }
            
            System.out.println("Fetched " + students.size() + " students from database");
            
        } catch (SQLException e) {
            System.err.println("Error fetching all students: " + e.getMessage());
            e.printStackTrace();
        }
        
        return students;
    }

    public List<Student> getStudentsByTeacher(int teacherId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE added_by = ? ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("student_id"),
                    rs.getString("roll_number"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("mother_name"),
                    rs.getInt("added_by"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                students.add(student);
            }
            
            System.out.println("Fetched " + students.size() + " students for teacher ID: " + teacherId);
            
        } catch (SQLException e) {
            System.err.println("Error fetching students by teacher: " + e.getMessage());
            e.printStackTrace();
        }
        
        return students;
    }

    public List<Student> searchStudentsByName(String searchTerm) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE student_name LIKE ? ORDER BY student_name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getInt("student_id"),
                    rs.getString("roll_number"),
                    rs.getString("student_name"),
                    rs.getString("email"),
                    rs.getString("mother_name"),
                    rs.getInt("added_by"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                students.add(student);
            }
            
            System.out.println("Found " + students.size() + " students matching: " + searchTerm);
            
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
            e.printStackTrace();
        }
        
        return students;
    }

    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET roll_number = ?, student_name = ?, email = ?, mother_name = ? WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, student.getRollNumber());
            pstmt.setString(2, student.getStudentName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMotherName());
            pstmt.setInt(5, student.getStudentId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Student updated successfully: " + student.getRollNumber());
                return true;
            } else {
                System.out.println("No student found with ID: " + student.getStudentId());
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        String query = "DELETE FROM students WHERE student_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, studentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Student deleted successfully - ID: " + studentId);
                return true;
            } else {
                System.out.println("No student found with ID: " + studentId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudentByRollNumber(String rollNumber) {
        String query = "DELETE FROM students WHERE roll_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, rollNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Student deleted successfully - Roll No: " + rollNumber);
                return true;
            } else {
                System.out.println("No student found with roll number: " + rollNumber);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRollNumberExists(String rollNumber) {
        String query = "SELECT COUNT(*) FROM students WHERE roll_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, rollNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking roll number: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public int getStudentCount() {
        String query = "SELECT COUNT(*) FROM students";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting student count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    public int getStudentCountByTeacher(int teacherId) {
        String query = "SELECT COUNT(*) FROM students WHERE added_by = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, teacherId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting student count by teacher: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("StudentDAO connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing StudentDAO connection: " + e.getMessage());
        }
    }
}
