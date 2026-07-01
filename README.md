# 🎓 Student Result Management System (Java Swing)

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk)
![Swing](https://img.shields.io/badge/Java-Swing-blue?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-Connectivity-success?style=for-the-badge)

A **desktop-based Student Result Management System** developed using **Java Swing**, **JDBC**, and **MySQL**. The application provides an intuitive graphical interface for managing students, teachers, and examination results while demonstrating core Java concepts including Object-Oriented Programming, DAO Design Pattern, multithreading, and file handling.

---

# 📖 Overview

The application provides separate interfaces for teachers and students.

Teachers can:

- Manage student records
- Enter and update examination results
- Generate academic reports
- Track system activities

Students can:

- View personal academic records
- Check grades and performance

The project follows a layered architecture to separate the presentation, business, and data access layers, improving maintainability and scalability.

---

# ✨ Features

## 👨‍🏫 Teacher Module

- Add Student Records
- Update Student Information
- Delete Student Records
- Search Students
- Enter Examination Marks
- Update Results
- Generate Reports

---

## 👨‍🎓 Student Module

- View Result
- View Grades
- Check Academic Performance

---

## 📊 Result Processing

- Automatic Percentage Calculation
- Grade Calculation
- Result Storage
- Performance Evaluation

---

## 💾 Database Operations

- MySQL Integration
- JDBC Connectivity
- CRUD Operations
- DAO Pattern Implementation

---

## 📂 File Handling

- Export Student Results
- Backup Academic Records
- Report Generation

---

## ⚡ Background Activity Logger

- Multithreaded Logging
- Activity Tracking
- Event Recording

---

# 🏗 System Architecture

```
                 User
                   │
                   ▼
            Java Swing GUI
                   │
                   ▼
          Business Logic Layer
                   │
                   ▼
               DAO Layer
                   │
                   ▼
            JDBC Connection
                   │
                   ▼
             MySQL Database
```

---

# 📂 Project Structure

```
StudentResultSystem_Swing

├── src
│
├── com.studentresult
│
├── Main.java
├── MainFrame.java
│
├── console
│   ├── StudentConsole.java
│   └── TeacherConsole.java
│
├── dao
│   ├── StudentDAO.java
│   ├── TeacherDAO.java
│   ├── ResultDAO.java
│   └── ActivityLogDAO.java
│
├── database
│   ├── DatabaseConnection.java
│   └── DatabaseConfig.java
│
├── model
│   ├── Student.java
│   ├── Teacher.java
│   └── Result.java
│
├── thread
│   └── ActivityLogger.java
│
└── util
    ├── GradeCalculator.java
    ├── FileHandler.java
    └── ValidationUtil.java
```

---

# 🛠 Technology Stack

### Programming Language

- Java

### User Interface

- Java Swing

### Database

- MySQL

### Connectivity

- JDBC

### Design Pattern

- DAO (Data Access Object)

### Core Java Concepts

- Object-Oriented Programming
- Exception Handling
- Collections
- Multithreading
- File Handling

---

# 💾 Database

The application uses MySQL for persistent storage.

Main tables include:

- Student
- Teacher
- Result
- ActivityLog

---

# 🚀 Getting Started

## Clone the Repository

```bash
git clone https://github.com/Curiousaryan/StudentResultSystem_Swing.git
```

---

## Configure Database

Create a MySQL database.

```sql
CREATE DATABASE student_result_system;
```

Update the database credentials in:

```
DatabaseConfig.java
```

```java
DB_URL=jdbc:mysql://localhost:3306/student_result_system
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

---

## Compile

```bash
javac -d bin src/**/*.java
```

---

## Run

```bash
java com.studentresult.Main
```

---

# 📊 Application Workflow

```
Launch Application
        │
        ▼
     Main Window
        │
        ▼
Select Teacher / Student
        │
 ┌──────┴──────┐
 │             │
 ▼             ▼
Teacher      Student
 │             │
 ▼             ▼
Manage       View Results
Records
 │
 ▼
MySQL Database
```

---

# 📚 Concepts Demonstrated

- Java Swing GUI Development
- JDBC Database Connectivity
- MySQL Integration
- DAO Design Pattern
- Object-Oriented Programming
- Layered Architecture
- File Handling
- Multithreading
- Exception Handling
- Input Validation

---

# 🎯 Learning Outcomes

This project demonstrates practical experience with:

- Desktop Application Development
- Database Programming
- MVC-inspired Architecture
- Java Collections
- CRUD Operations
- Academic Record Management

---

# 🚀 Future Enhancements

- User Authentication
- Password Encryption
- Admin Dashboard
- Attendance Management
- PDF Report Generation
- Email Notifications
- Charts & Analytics
- Dark Mode UI
- Maven Support
- Unit Testing with JUnit

---

# 📸 Screenshots

Add screenshots for:

- Home Screen
- Teacher Dashboard
- Student Dashboard
- Result Entry Form
- Result Display
- Database Tables

---

# 👨‍💻 Author

**Aryan**

Java Backend Developer | Spring Boot Developer | AI Enthusiast

**GitHub:** https://github.com/Curiousaryan

---

# ⭐ Support

If you found this project useful, consider giving it a **Star ⭐** on GitHub.
