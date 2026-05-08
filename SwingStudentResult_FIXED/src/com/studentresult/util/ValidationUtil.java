package com.studentresult.util;

import java.util.regex.Pattern;
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern ROLL_NUMBER_PATTERN = 
        Pattern.compile("^[A-Z][0-9]{9}$");
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[A-Za-z\\s]+$");
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[A-Za-z0-9_-]{3,20}$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;

    private ValidationUtil() {
        throw new AssertionError("Cannot instantiate ValidationUtil utility class");
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format. Example: user@example.com";
        }
        return null; // Valid
    }

    public static boolean isValidRollNumber(String rollNumber) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            return false;
        }
        return ROLL_NUMBER_PATTERN.matcher(rollNumber.trim().toUpperCase()).matches();
    }

    public static String validateRollNumber(String rollNumber) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            return "Roll number cannot be empty";
        }
        if (rollNumber.trim().length() != 10) {
            return "Roll number must be 10 characters long";
        }
        if (!isValidRollNumber(rollNumber)) {
            return "Invalid roll number format. Example: T400810438 (1 letter + 9 digits)";
        }
        return null; // Valid
    }

    public static String formatRollNumber(String rollNumber) {
        if (rollNumber == null) return null;
        return rollNumber.trim().toUpperCase();
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.length() < MIN_NAME_LENGTH || trimmedName.length() > MAX_NAME_LENGTH) {
            return false;
        }
        
        return NAME_PATTERN.matcher(trimmedName).matches();
    }

    public static String validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            return fieldName + " cannot be empty";
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.length() < MIN_NAME_LENGTH) {
            return fieldName + " must be at least " + MIN_NAME_LENGTH + " characters long";
        }
        
        if (trimmedName.length() > MAX_NAME_LENGTH) {
            return fieldName + " cannot exceed " + MAX_NAME_LENGTH + " characters";
        }
        
        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            return fieldName + " should contain only letters and spaces";
        }
        
        return null;
    }

    public static String formatName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }
        
        String[] words = name.trim().toLowerCase().split("\\s+");
        StringBuilder formatted = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1))
                         .append(" ");
            }
        }
        
        return formatted.toString().trim();
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    public static String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty";
        }
        
        String trimmed = username.trim();
        
        if (trimmed.length() < 3) {
            return "Username must be at least 3 characters long";
        }
        
        if (trimmed.length() > 20) {
            return "Username cannot exceed 20 characters";
        }
        
        if (!USERNAME_PATTERN.matcher(trimmed).matches()) {
            return "Username can only contain letters, numbers, underscore, and hyphen";
        }
        
        return null;
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        int length = password.length();
        return length >= MIN_PASSWORD_LENGTH && length <= MAX_PASSWORD_LENGTH;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long";
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "Password cannot exceed " + MAX_PASSWORD_LENGTH + " characters";
        }
        
        return null;
    }

    public static String getPasswordStrength(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return "Weak";
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        int criteriasMet = (hasUpper ? 1 : 0) + (hasLower ? 1 : 0) + 
                          (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);
        
        if (criteriasMet >= 3 && password.length() >= 8) {
            return "Strong";
        } else if (criteriasMet >= 2 || password.length() >= 8) {
            return "Medium";
        } else {
            return "Weak";
        }
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number cannot be empty";
        }
        
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            return "Invalid phone number. Must be 10 digits";
        }
        
        return null; 
    }

    public static boolean isValidMarks(double marks) {
        return marks >= 0 && marks <= 100;
    }

    public static String validateMarks(double marks, String subjectName) {
        if (marks < 0) {
            return subjectName + " marks cannot be negative";
        }
        
        if (marks > 100) {
            return subjectName + " marks cannot exceed 100";
        }
        
        return null; // Valid
    }

    public static String validateMarksInput(String marksString) {
        if (marksString == null || marksString.trim().isEmpty()) {
            return "Marks cannot be empty";
        }
        
        try {
            double marks = Double.parseDouble(marksString.trim());
            return validateMarks(marks, "");
        } catch (NumberFormatException e) {
            return "Invalid marks. Please enter a valid number";
        }
    }

    public static boolean isValidInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDouble(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int parseIntSafe(String str, int defaultValue) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    public static double parseDoubleSafe(String str, double defaultValue) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }
    

    public static String sanitizeInput(String input) {
        if (input == null) return null;
        return input.trim().replaceAll("\\s+", " ");
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("VALIDATION UTIL TEST");
        System.out.println("========================================\n");
        
        System.out.println("Email Validation:");
        System.out.println("john@example.com: " + isValidEmail("john@example.com"));
        System.out.println("invalid-email: " + isValidEmail("invalid-email"));
        System.out.println();
        
        System.out.println("Roll Number Validation:");
        System.out.println("T400810438: " + isValidRollNumber("T400810438"));
        System.out.println("ABC123: " + isValidRollNumber("ABC123"));
        System.out.println();
        
        System.out.println("Name Validation:");
        System.out.println("John Doe: " + isValidName("John Doe"));
        System.out.println("Formatted: " + formatName("john doe"));
        System.out.println("John123: " + isValidName("John123"));
        System.out.println();
        
        System.out.println("Username Validation:");
        System.out.println("john_doe: " + isValidUsername("john_doe"));
        System.out.println("ab: " + isValidUsername("ab"));
        System.out.println();
        
        System.out.println("Password Validation:");
        System.out.println("pass123: " + isValidPassword("pass123"));
        System.out.println("Strength of 'Pass@123': " + getPasswordStrength("Pass@123"));
        System.out.println();
        
        System.out.println("Marks Validation:");
        System.out.println("85.5: " + isValidMarks(85.5));
        System.out.println("150: " + isValidMarks(150));
        System.out.println("-10: " + isValidMarks(-10));
        
        System.out.println("\n========================================");
        System.out.println("TEST COMPLETED");
        System.out.println("========================================");
    }
}
