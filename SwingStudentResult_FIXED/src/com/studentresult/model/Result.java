package com.studentresult.model;

import java.time.LocalDateTime;

public class Result {
	private int resultId;
	private int studentId;
	private double ANN_marks;
	private double CNN_marks;
	private double JAVA_marks;
	private double FAIML_marks;
	private double IOT_marks;
	private double totalMarks;
	private double percentage;
	private String grade;
	private String resultStatus;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	public Result() {
		
	}
	public Result(int studentId, double ann_marks, double cnn_marks,
            double java_marks, double faiml_marks, double iot_marks) {
  this.studentId = studentId;
  this.ANN_marks = ann_marks;
  this.CNN_marks = cnn_marks;
  this.JAVA_marks = java_marks;
  this.FAIML_marks = faiml_marks;
  this.IOT_marks = iot_marks;
  
  calculateResult();
}
	public Result(int studentId, double aNN_marks, double cNN_marks, double jAVA_marks, double fAIML_marks,
			double iOT_marks, double totalMarks, double percentage, String grade, String resultStatus,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.studentId = studentId;
		ANN_marks = aNN_marks;
		CNN_marks = cNN_marks;
		JAVA_marks = jAVA_marks;
		FAIML_marks = fAIML_marks;
		IOT_marks = iOT_marks;
		this.totalMarks = totalMarks;
		this.percentage = percentage;
		this.grade = grade;
		this.resultStatus = resultStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public Result(int resultId, int studentId, double aNN_marks, double cNN_marks, double jAVA_marks,
			double fAIML_marks, double iOT_marks, double totalMarks, double percentage, String grade,
			String resultStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.resultId = resultId;
		this.studentId = studentId;
		ANN_marks = aNN_marks;
		CNN_marks = cNN_marks;
		JAVA_marks = jAVA_marks;
		FAIML_marks = fAIML_marks;
		IOT_marks = iOT_marks;
		this.totalMarks = totalMarks;
		this.percentage = percentage;
		this.grade = grade;
		this.resultStatus = resultStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	private String calculateGrade(double percentage) {
        if (percentage >= 90) {
            return "A+";
        } else if (percentage >= 80) {
            return "A";
        } else if (percentage >= 70) {
            return "B+";
        } else if (percentage >= 60) {
            return "B";
        } else if (percentage >= 50) {
            return "C";
        } else if (percentage >= 40) {
            return "D";
        } else {
            return "F";
        }
    }
	
	private String determineResultStatus() {
        double passingMarks = 33.0;
        
        if (ANN_marks >= passingMarks && 
        	CNN_marks >= passingMarks && 
        	JAVA_marks >= passingMarks && 
        	FAIML_marks >= passingMarks && 
        	IOT_marks >= passingMarks) {
            return "PASS";
        } else {
            return "FAIL";
        }
    }
	
	public void calculateResult() {
		this.totalMarks = ANN_marks + CNN_marks + JAVA_marks + FAIML_marks + IOT_marks;
		this.percentage = (totalMarks/500.0)*100.0;
		this.grade = calculateGrade(this.percentage);
		this.resultStatus = determineResultStatus();
	}
	
	public int getResultId() {
		return resultId;
	}
	public void setResultId(int resultId) {
		this.resultId = resultId;
	}
	public int getStudentId() {
		return studentId;
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
	public double getANN_marks() {
		return ANN_marks;
	}
	public void setANN_marks(double aNN_marks) {
		ANN_marks = aNN_marks;
	}
	public double getCNN_marks() {
		return CNN_marks;
	}
	public void setCNN_marks(double cNN_marks) {
		CNN_marks = cNN_marks;
	}
	public double getJAVA_marks() {
		return JAVA_marks;
	}
	public void setJAVA_marks(double jAVA_marks) {
		JAVA_marks = jAVA_marks;
	}
	public double getFAIML_marks() {
		return FAIML_marks;
	}
	public void setFAIML_marks(double fAIML_marks) {
		FAIML_marks = fAIML_marks;
	}
	public double getIOT_marks() {
		return IOT_marks;
	}
	public void setIOT_marks(double iOT_marks) {
		IOT_marks = iOT_marks;
	}
	public double getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(double totalMarks) {
		this.totalMarks = totalMarks;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@Override
    public String toString() {
        return "Result{" +
                "resultId=" + resultId +
                ", studentId=" + studentId +
                ", subject1Marks=" + ANN_marks +
                ", subject2Marks=" + CNN_marks +
                ", subject3Marks=" + JAVA_marks +
                ", subject4Marks=" + FAIML_marks +
                ", subject5Marks=" + IOT_marks +
                ", totalMarks=" + totalMarks +
                ", percentage=" + String.format("%.2f", percentage) +
                ", grade='" + grade + '\'' +
                ", resultStatus='" + resultStatus + '\'' +
                '}';
    }
	
	public String displayResult() {
        StringBuilder result = new StringBuilder();
        result.append("\n╔════════════════════════════════════════════════════╗\n");
        result.append("║              STUDENT RESULT CARD                   ║\n");
        result.append("╠════════════════════════════════════════════════════╣\n");
        result.append(String.format("║ Subject 1        : %6.2f / 100                  ║\n", ANN_marks));
        result.append(String.format("║ Subject 2        : %6.2f / 100                  ║\n", CNN_marks));
        result.append(String.format("║ Subject 3        : %6.2f / 100                  ║\n", JAVA_marks));
        result.append(String.format("║ Subject 4        : %6.2f / 100                  ║\n", FAIML_marks));
        result.append(String.format("║ Subject 5        : %6.2f / 100                  ║\n", IOT_marks));
        result.append("╠════════════════════════════════════════════════════╣\n");
        result.append(String.format("║ Total Marks      : %6.2f / 500                  ║\n", totalMarks));
        result.append(String.format("║ Percentage       : %6.2f%%                       ║\n", percentage));
        result.append(String.format("║ Grade            : %-4s                           ║\n", grade));
        result.append(String.format("║ Result Status    : %-6s                         ║\n", resultStatus));
        result.append("╚════════════════════════════════════════════════════╝\n");
        return result.toString();
    }
	
	public boolean isValid() {
        return isValidMarks(ANN_marks) &&
               isValidMarks(CNN_marks) &&
               isValidMarks(JAVA_marks) &&
               isValidMarks(FAIML_marks) &&
               isValidMarks(IOT_marks);
    }
	
	private boolean isValidMarks(double marks) {
        return marks >= 0 && marks <= 100;
    }
}
