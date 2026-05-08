package com.studentresult.model;

import java.time.LocalDateTime;

public class Teacher {
    private int teacherId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;
    public Teacher() {
    	
    }
    
    public Teacher(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }
    
    public Teacher(int teacherId, String username, String password, 
                   String fullName, String email, LocalDateTime createdAt) {
        this.teacherId = teacherId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", username='" + username + '\'' +
                ", password='***'" + // Password masked
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public String displayInfo() {
        StringBuilder info = new StringBuilder();
        info.append("\n╔════════════════════════════════════════╗\n");
        info.append("║         TEACHER INFORMATION            ║\n");
        info.append("╠════════════════════════════════════════╣\n");
        info.append(String.format("║ Teacher ID  : %-24d ║\n", teacherId));
        info.append(String.format("║ Username    : %-24s ║\n", username));
        info.append(String.format("║ Full Name   : %-24s ║\n", fullName));
        info.append(String.format("║ Email       : %-24s ║\n", email));
        info.append("╚════════════════════════════════════════╝\n");
        return info.toString();
    }
    boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               fullName != null && !fullName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty();
    }

    boolean verifyCredentials(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && 
               this.password.equals(inputPassword);
    }
    

    public String getMaskedPassword() {
        if (password != null) {
            return "*".repeat(password.length());
        }
        return "";
    }
}