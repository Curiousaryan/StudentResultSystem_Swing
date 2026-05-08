# Student Result Management System — Swing + JDBC

## Package Structure (unchanged from original)

```
src/
└── com/studentresult/
    ├── Main.java                        ← Swing entry point
    ├── MainFrame.java                   ← Root JFrame with CardLayout
    ├── console/
    │   ├── TeacherConsole.java          ← Swing teacher dashboard (was TeacherConsole)
    │   └── StudentConsole.java          ← Swing student portal (was StudentConsole)
    ├── dao/
    │   ├── ActivityLogDAO.java          ← unchanged
    │   ├── ResultDAO.java               ← unchanged
    │   ├── StudentDAO.java              ← unchanged
    │   └── TeacherDAO.java              ← unchanged
    ├── database/
    │   ├── DatabaseConfig.java          ← unchanged  (edit DB creds here)
    │   └── DatabaseConnection.java      ← unchanged
    ├── model/
    │   ├── Result.java                  ← unchanged
    │   ├── Student.java                 ← unchanged
    │   └── Teacher.java                 ← unchanged
    ├── thread/
    │   └── ActivityLogger.java          ← unchanged
    └── util/
        ├── FileHandler.java             ← unchanged
        ├── GradeCalculator.java         ← unchanged
        └── ValidationUtil.java          ← unchanged
```

## Prerequisites

- Java 11+
- MySQL 8+ with the `student_result_system` database
- `mysql-connector-java.jar` on the classpath

## Database Credentials

Edit `src/com/studentresult/database/DatabaseConfig.java`:

```java
public static final String DB_URL      = "jdbc:mysql://localhost:3306/student_result_system";
public static final String DB_USERNAME = "root";
public static final String DB_PASSWORD = "your_password";
```

## Compile & Run (command line)

```bash
# From project root
javac -cp "lib/mysql-connector-java.jar" \
      -d bin \
      $(find src -name "*.java")

java -cp "bin:lib/mysql-connector-java.jar" com.studentresult.Main
```

## IDE (Eclipse / IntelliJ)

1. Import as Java project.
2. Add `mysql-connector-java.jar` to the build path.
3. Run `com.studentresult.Main`.

## GUI Overview

| Screen             | Description                                      |
|--------------------|--------------------------------------------------|
| Welcome Screen     | Home page with Teacher / Student / Exit buttons  |
| Teacher Login      | Username + password authentication               |
| Teacher Dashboard  | 4-tab panel: Students · Results · Reports · Logs |
| Students Tab       | Add / Update / Delete / Search students (table)  |
| Results Tab        | Add / Update / Delete results; Backup all        |
| Reports Tab        | Stats: pass rate, avg %, top 10 performers       |
| Activity Logs Tab  | Last 200 DB activity log entries                 |
| Student Portal     | Roll number + mother's name → result card        |

## Subjects

| Code  | Full Name                     |
|-------|-------------------------------|
| ANN   | Artificial Neural Networks    |
| CNN   | Computer Neural Networks      |
| JAVA  | Java Programming              |
| FAIML | Fundamentals of AI & ML       |
| IOT   | Internet of Things            |

Passing marks per subject: **33 / 100**


## Fixed Build Instructions (UTF-8)

### Windows CMD

```cmd
dir /s /B src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
java -cp bin com.studentresult.Main
```

### Linux / macOS

```bash
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
java -cp bin com.studentresult.Main
```

## Important

If database connection fails, install MySQL Connector/J:
https://dev.mysql.com/downloads/connector/j/
