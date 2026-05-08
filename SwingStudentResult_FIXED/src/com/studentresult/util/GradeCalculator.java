package com.studentresult.util;

public class GradeCalculator {
    public static final double MAX_MARKS_PER_SUBJECT = 100.0;
    public static final int TOTAL_SUBJECTS = 5;
    public static final double MAX_TOTAL_MARKS = MAX_MARKS_PER_SUBJECT * TOTAL_SUBJECTS;
    public static final double PASSING_MARKS = 33.0;
    
    public static final double GRADE_A_PLUS = 90.0;
    public static final double GRADE_A = 80.0;
    public static final double GRADE_B_PLUS = 70.0;
    public static final double GRADE_B = 60.0;
    public static final double GRADE_C = 50.0;
    public static final double GRADE_D = 40.0;
    
    // Subject Names
    public static final String SUBJECT_1_NAME = "ANN";
    public static final String SUBJECT_2_NAME = "CNN";
    public static final String SUBJECT_3_NAME = "JAVA";
    public static final String SUBJECT_4_NAME = "FAIML";
    public static final String SUBJECT_5_NAME = "IOT";
    
    public static final String SUBJECT_1_FULL = "Artificial Neural Networks";
    public static final String SUBJECT_2_FULL = "Computer Neural Networks";
    public static final String SUBJECT_3_FULL = "Java Programming";
    public static final String SUBJECT_4_FULL = "Fundamentals of AI & ML";
    public static final String SUBJECT_5_FULL = "Internet of Things";

    private GradeCalculator() {
        throw new AssertionError("Cannot instantiate GradeCalculator utility class");
    }

    public static double calculateTotal(double ann_marks, double cnn_marks, 
                                       double java_marks, double faiml_marks, 
                                       double iot_marks) {
        return ann_marks + cnn_marks + java_marks + faiml_marks + iot_marks;
    }

    public static double calculateTotal(double[] marks) {
        if (marks == null || marks.length != TOTAL_SUBJECTS) {
            throw new IllegalArgumentException("Marks array must contain exactly 5 subjects");
        }
        
        double total = 0;
        for (double mark : marks) {
            total += mark;
        }
        return total;
    }
    

    public static double calculatePercentage(double totalMarks) {
        return (totalMarks / MAX_TOTAL_MARKS) * 100.0;
    }

    public static double calculatePercentage(double ann_marks, double cnn_marks, 
                                            double java_marks, double faiml_marks, 
                                            double iot_marks) {
        double total = calculateTotal(ann_marks, cnn_marks, java_marks, faiml_marks, iot_marks);
        return calculatePercentage(total);
    }

    public static String calculateGrade(double percentage) {
        if (percentage >= GRADE_A_PLUS) {
            return "A+";
        } else if (percentage >= GRADE_A) {
            return "A";
        } else if (percentage >= GRADE_B_PLUS) {
            return "B+";
        } else if (percentage >= GRADE_B) {
            return "B";
        } else if (percentage >= GRADE_C) {
            return "C";
        } else if (percentage >= GRADE_D) {
            return "D";
        } else {
            return "F";
        }
    }

    public static double getGradePoints(String grade) {
        switch (grade.toUpperCase()) {
            case "A+": return 10.0;
            case "A":  return 9.0;
            case "B+": return 8.0;
            case "B":  return 7.0;
            case "C":  return 6.0;
            case "D":  return 5.0;
            case "F":  return 0.0;
            default:   return 0.0;
        }
    }

    public static String determineResultStatus(double ann_marks, double cnn_marks, 
                                               double java_marks, double faiml_marks, 
                                               double iot_marks) {
        if (ann_marks >= PASSING_MARKS && 
            cnn_marks >= PASSING_MARKS && 
            java_marks >= PASSING_MARKS && 
            faiml_marks >= PASSING_MARKS && 
            iot_marks >= PASSING_MARKS) {
            return "PASS";
        } else {
            return "FAIL";
        }
    }

    public static String determineResultStatus(double[] marks) {
        if (marks == null || marks.length != TOTAL_SUBJECTS) {
            return "FAIL";
        }
        
        for (double mark : marks) {
            if (mark < PASSING_MARKS) {
                return "FAIL";
            }
        }
        return "PASS";
    }

    public static boolean isPassed(double ann_marks, double cnn_marks, 
                                   double java_marks, double faiml_marks, 
                                   double iot_marks) {
        return determineResultStatus(ann_marks, cnn_marks, java_marks, faiml_marks, iot_marks).equals("PASS");
    }

    public static boolean isValidMarks(double marks) {
        return marks >= 0 && marks <= MAX_MARKS_PER_SUBJECT;
    }

    public static boolean areAllMarksValid(double ann_marks, double cnn_marks, 
                                          double java_marks, double faiml_marks, 
                                          double iot_marks) {
        return isValidMarks(ann_marks) && 
               isValidMarks(cnn_marks) && 
               isValidMarks(java_marks) && 
               isValidMarks(faiml_marks) && 
               isValidMarks(iot_marks);
    }

    public static boolean areAllMarksValid(double[] marks) {
        if (marks == null || marks.length != TOTAL_SUBJECTS) {
            return false;
        }
        
        for (double mark : marks) {
            if (!isValidMarks(mark)) {
                return false;
            }
        }
        return true;
    }

    public static String getGradeDescription(String grade) {
        switch (grade.toUpperCase()) {
            case "A+": return "Outstanding";
            case "A":  return "Excellent";
            case "B+": return "Very Good";
            case "B":  return "Good";
            case "C":  return "Average";
            case "D":  return "Below Average";
            case "F":  return "Fail";
            default:   return "Unknown";
        }
    }

    public static double getHighestMarks(double ann_marks, double cnn_marks, 
                                        double java_marks, double faiml_marks, 
                                        double iot_marks) {
        double max = ann_marks;
        if (cnn_marks > max) max = cnn_marks;
        if (java_marks > max) max = java_marks;
        if (faiml_marks > max) max = faiml_marks;
        if (iot_marks > max) max = iot_marks;
        return max;
    }

    public static double getLowestMarks(double ann_marks, double cnn_marks, 
                                       double java_marks, double faiml_marks, 
                                       double iot_marks) {
        double min = ann_marks;
        if (cnn_marks < min) min = cnn_marks;
        if (java_marks < min) min = java_marks;
        if (faiml_marks < min) min = faiml_marks;
        if (iot_marks < min) min = iot_marks;
        return min;
    }

    public static int getPassedSubjectCount(double ann_marks, double cnn_marks, 
                                           double java_marks, double faiml_marks, 
                                           double iot_marks) {
        int count = 0;
        if (ann_marks >= PASSING_MARKS) count++;
        if (cnn_marks >= PASSING_MARKS) count++;
        if (java_marks >= PASSING_MARKS) count++;
        if (faiml_marks >= PASSING_MARKS) count++;
        if (iot_marks >= PASSING_MARKS) count++;
        return count;
    }

    public static int getFailedSubjectCount(double ann_marks, double cnn_marks, 
                                           double java_marks, double faiml_marks, 
                                           double iot_marks) {
        return TOTAL_SUBJECTS - getPassedSubjectCount(ann_marks, cnn_marks, java_marks, faiml_marks, iot_marks);
    }

    public static String getSubjectName(int subjectNumber) {
        switch (subjectNumber) {
            case 1: return SUBJECT_1_NAME;
            case 2: return SUBJECT_2_NAME;
            case 3: return SUBJECT_3_NAME;
            case 4: return SUBJECT_4_NAME;
            case 5: return SUBJECT_5_NAME;
            default: return "Unknown";
        }
    }

    public static String getSubjectFullName(int subjectNumber) {
        switch (subjectNumber) {
            case 1: return SUBJECT_1_FULL;
            case 2: return SUBJECT_2_FULL;
            case 3: return SUBJECT_3_FULL;
            case 4: return SUBJECT_4_FULL;
            case 5: return SUBJECT_5_FULL;
            default: return "Unknown Subject";
        }
    }
    
    public static String formatPercentage(double percentage) {
        return String.format("%.2f%%", percentage);
    }

    public static String formatMarks(double marks) {
        return String.format("%.2f", marks);
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("GRADE CALCULATOR TEST");
        System.out.println("========================================");
        System.out.println();
        
        double ann = 85.5;
        double cnn = 92.0;
        double java = 78.5;
        double faiml = 88.0;
        double iot = 91.5;
        
        System.out.println("Subject Marks:");
        System.out.println(SUBJECT_1_NAME + " (" + SUBJECT_1_FULL + "): " + ann);
        System.out.println(SUBJECT_2_NAME + " (" + SUBJECT_2_FULL + "): " + cnn);
        System.out.println(SUBJECT_3_NAME + " (" + SUBJECT_3_FULL + "): " + java);
        System.out.println(SUBJECT_4_NAME + " (" + SUBJECT_4_FULL + "): " + faiml);
        System.out.println(SUBJECT_5_NAME + " (" + SUBJECT_5_FULL + "): " + iot);
        System.out.println();
        
        double total = calculateTotal(ann, cnn, java, faiml, iot);
        double percentage = calculatePercentage(total);
        String grade = calculateGrade(percentage);
        String status = determineResultStatus(ann, cnn, java, faiml, iot);
        
        System.out.println("Calculated Results:");
        System.out.println("Total Marks: " + formatMarks(total) + " / " + MAX_TOTAL_MARKS);
        System.out.println("Percentage: " + formatPercentage(percentage));
        System.out.println("Grade: " + grade + " (" + getGradeDescription(grade) + ")");
        System.out.println("Status: " + status);
        System.out.println("Grade Points: " + getGradePoints(grade));
        System.out.println();
        
        System.out.println("Analysis:");
        System.out.println("Highest Marks: " + getHighestMarks(ann, cnn, java, faiml, iot));
        System.out.println("Lowest Marks: " + getLowestMarks(ann, cnn, java, faiml, iot));
        System.out.println("Subjects Passed: " + getPassedSubjectCount(ann, cnn, java, faiml, iot) + " / " + TOTAL_SUBJECTS);
        System.out.println("Subjects Failed: " + getFailedSubjectCount(ann, cnn, java, faiml, iot) + " / " + TOTAL_SUBJECTS);
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST COMPLETED");
        System.out.println("========================================");
    }
}
