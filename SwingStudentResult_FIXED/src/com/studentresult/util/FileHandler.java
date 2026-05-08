package com.studentresult.util;

import com.studentresult.model.Student;
import com.studentresult.model.Result;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileHandler {
    
    // Constants
    public static final String BACKUP_DIR = "data/backup/";
    public static final String RESULTS_DIR = "data/results/";
    public static final String LOGS_DIR = "data/logs/";
    
    private static final DateTimeFormatter FILE_DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = 
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    // Private constructor
    private FileHandler() {
        throw new AssertionError("Cannot instantiate FileHandler utility class");
    }
    
    public static boolean initializeDirectories() {
        try {
            File backupDir = new File(BACKUP_DIR);
            File resultsDir = new File(RESULTS_DIR);
            File logsDir = new File(LOGS_DIR);
            
            if (!backupDir.exists()) backupDir.mkdirs();
            if (!resultsDir.exists()) resultsDir.mkdirs();
            if (!logsDir.exists()) logsDir.mkdirs();
            
            System.out.println("File directories initialized successfully");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error initializing directories: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public static String backupResultToTextFile(Student student, Result result) {
        initializeDirectories();
        
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
        String fileName = "Result_" + student.getRollNumber() + "_" + timestamp + ".txt";
        String filePath = RESULTS_DIR + fileName;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            
            // Write header
            writer.write("========================================\n");
            writer.write("       STUDENT RESULT REPORT\n");
            writer.write("========================================\n");
            writer.write("Generated: " + LocalDateTime.now().format(DISPLAY_DATE_FORMAT) + "\n");
            writer.write("========================================\n\n");
            
            // Write student information
            writer.write("--- STUDENT INFORMATION ---\n");
            writer.write("Roll Number    : " + student.getRollNumber() + "\n");
            writer.write("Name           : " + student.getStudentName() + "\n");
            writer.write("Email          : " + student.getEmail() + "\n");
            writer.write("Mother's Name  : " + student.getMotherName() + "\n\n");
            
            // Write marks
            writer.write("--- SUBJECT MARKS ---\n");
            writer.write(String.format("%-30s : %6.2f / 100\n", 
                GradeCalculator.SUBJECT_1_FULL, result.getANN_marks()));
            writer.write(String.format("%-30s : %6.2f / 100\n", 
                GradeCalculator.SUBJECT_2_FULL, result.getCNN_marks()));
            writer.write(String.format("%-30s : %6.2f / 100\n", 
                GradeCalculator.SUBJECT_3_FULL, result.getJAVA_marks()));
            writer.write(String.format("%-30s : %6.2f / 100\n", 
                GradeCalculator.SUBJECT_4_FULL, result.getFAIML_marks()));
            writer.write(String.format("%-30s : %6.2f / 100\n\n", 
                GradeCalculator.SUBJECT_5_FULL, result.getIOT_marks()));
            
            // Write result summary
            writer.write("--- RESULT SUMMARY ---\n");
            writer.write(String.format("Total Marks    : %.2f / 500\n", result.getTotalMarks()));
            writer.write(String.format("Percentage     : %.2f%%\n", result.getPercentage()));
            writer.write(String.format("Grade          : %s (%s)\n", 
                result.getGrade(), GradeCalculator.getGradeDescription(result.getGrade())));
            writer.write(String.format("Result Status  : %s\n\n", result.getResultStatus()));
            
            // Write footer
            writer.write("========================================\n");
            writer.write("    END OF RESULT REPORT\n");
            writer.write("========================================\n");
            
            System.out.println("Result backed up to: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Error backing up result to text file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String backupMultipleResultsToTextFile(
            List<StudentResultPair> studentResultList, String fileName) {
        
        initializeDirectories();
        
        if (fileName == null || fileName.trim().isEmpty()) {
            String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
            fileName = "AllResults_" + timestamp + ".txt";
        }
        
        String filePath = BACKUP_DIR + fileName;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            
            // Write header
            writer.write("========================================\n");
            writer.write("    ALL STUDENTS RESULTS BACKUP\n");
            writer.write("========================================\n");
            writer.write("Generated: " + LocalDateTime.now().format(DISPLAY_DATE_FORMAT) + "\n");
            writer.write("Total Students: " + studentResultList.size() + "\n");
            writer.write("========================================\n\n");
            
            // Write each student's result
            int count = 1;
            for (StudentResultPair pair : studentResultList) {
                Student student = pair.getStudent();
                Result result = pair.getResult();
                
                writer.write("--- STUDENT " + count + " ---\n");
                writer.write("Roll Number: " + student.getRollNumber() + "\n");
                writer.write("Name: " + student.getStudentName() + "\n");
                writer.write(String.format("Marks: %.2f, %.2f, %.2f, %.2f, %.2f\n", 
                    result.getANN_marks(), result.getCNN_marks(), 
                    result.getJAVA_marks(), result.getFAIML_marks(), 
                    result.getIOT_marks()));
                writer.write(String.format("Total: %.2f | Percentage: %.2f%% | Grade: %s | Status: %s\n", 
                    result.getTotalMarks(), result.getPercentage(), 
                    result.getGrade(), result.getResultStatus()));
                writer.write("\n");
                
                count++;
            }
            
            writer.write("========================================\n");
            writer.write("         END OF BACKUP\n");
            writer.write("========================================\n");
            
            System.out.println("Multiple results backed up to: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Error backing up multiple results: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String saveResultToDataFile(Student student, Result result) {
        initializeDirectories();
        
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
        String fileName = "Result_" + student.getRollNumber() + "_" + timestamp + ".dat";
        String filePath = RESULTS_DIR + fileName;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            
            StudentResultPair pair = new StudentResultPair(student, result);
            oos.writeObject(pair);
            
            System.out.println("Result saved to data file: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.err.println("Error saving result to data file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static StudentResultPair loadResultFromDataFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            
            StudentResultPair pair = (StudentResultPair) ois.readObject();
            
            System.out.println("Result loaded from data file: " + filePath);
            return pair;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading result from data file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized boolean appendToLogFile(String logMessage, String logFileName) {
        initializeDirectories();
        
        String filePath = LOGS_DIR + logFileName;
        
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath, true))) {
            
            String timestamp = LocalDateTime.now().format(DISPLAY_DATE_FORMAT);
            writer.write("[" + timestamp + "] " + logMessage + "\n");
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error appending to log file: " + e.getMessage());
            return false;
        }
    }

    public static boolean logActivity(String activityType, String description, String performedBy) {
        String logMessage = String.format("[%s] %s - %s", 
            activityType, performedBy, description);
        return appendToLogFile(logMessage, "activity.log");
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("✓ File deleted: " + filePath);
                }
                return deleted;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }
    }

    public static String[] listFiles(String directoryPath) {
        File directory = new File(directoryPath);
        
        if (!directory.exists() || !directory.isDirectory()) {
            return new String[0];
        }
        
        String[] files = directory.list();
        return files != null ? files : new String[0];
    }

    public static long getFileSize(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.length();
            }
        } catch (Exception e) {
            System.err.println("Error getting file size: " + e.getMessage());
        }
        return -1;
    }

    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }

    public static class StudentResultPair implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Student student;
        private Result result;
        
        public StudentResultPair(Student student, Result result) {
            this.student = student;
            this.result = result;
        }
        
        public Student getStudent() {
            return student;
        }
        
        public Result getResult() {
            return result;
        }
    }
}