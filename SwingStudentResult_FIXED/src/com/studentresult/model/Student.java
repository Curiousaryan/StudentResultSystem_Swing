package com.studentresult.model;

import java.time.LocalDateTime;

public class Student {
    
    private int studentId;
    private String rollNumber;
    private String studentName;
    private String email;
    private String motherName;
    private int addedBy;
    private LocalDateTime createdAt;
    

    public Student() {
    }
    
    public Student(String rollNumber, String studentName, String email, 
                   String motherName, int addedBy) {
        this.rollNumber = rollNumber;
        this.studentName = studentName;
        this.email = email;
        this.motherName = motherName;
        this.addedBy = addedBy;
    }
    
    public Student(int studentId, String rollNumber, String studentName, 
                   String email, String motherName, int addedBy, 
                   LocalDateTime createdAt) {
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.studentName = studentName;
        this.email = email;
        this.motherName = motherName;
        this.addedBy = addedBy;
        this.createdAt = createdAt;
    }
    
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getMotherName() { return motherName; }
    public void setMotherName(String motherName) { this.motherName = motherName; }
    
    public int getAddedBy() { return addedBy; }
    public void setAddedBy(int addedBy) { this.addedBy = addedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", rollNumber='" + rollNumber + '\'' +
                ", studentName='" + studentName + '\'' +
                ", email='" + email + '\'' +
                ", motherName='" + motherName + '\'' +
                ", addedBy=" + addedBy +
                ", createdAt=" + createdAt +
                '}';
    }
    
    public String displayInfo() {
        StringBuilder info = new StringBuilder();
        info.append("\n╔════════════════════════════════════════╗\n");
        info.append("║         STUDENT INFORMATION            ║\n");
        info.append("╠════════════════════════════════════════╣\n");
        info.append(String.format("║ Roll Number : %-24s ║\n", rollNumber));
        info.append(String.format("║ Name        : %-24s ║\n", studentName));
        info.append(String.format("║ Email       : %-24s ║\n", email));
        info.append(String.format("║ Mother Name : %-24s ║\n", motherName));
        info.append("╚════════════════════════════════════════╝\n");
        return info.toString();
    }
    
    public boolean isValid() {
        return rollNumber != null && !rollNumber.trim().isEmpty() &&
               studentName != null && !studentName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               motherName != null && !motherName.trim().isEmpty();
    }
}