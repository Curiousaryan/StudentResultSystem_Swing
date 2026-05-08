package com.studentresult.console;

import com.studentresult.MainFrame;
import com.studentresult.dao.ActivityLogDAO;
import com.studentresult.dao.ResultDAO;
import com.studentresult.dao.StudentDAO;
import com.studentresult.dao.TeacherDAO;
import com.studentresult.model.Result;
import com.studentresult.model.Student;
import com.studentresult.model.Teacher;
import com.studentresult.thread.ActivityLogger;
import com.studentresult.util.FileHandler;
import com.studentresult.util.GradeCalculator;
import com.studentresult.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Teacher console panel.
 * Shows a login screen first; after successful authentication a full
 * dashboard (JTabbedPane) with Students, Results, Reports and Activity Logs
 * is displayed.
 */
public class TeacherConsole extends JPanel {

    // ── DAOs ─────────────────────────────────────────────────────────────────
    private final TeacherDAO     teacherDAO  = new TeacherDAO();
    private final StudentDAO     studentDAO  = new StudentDAO();
    private final ResultDAO      resultDAO   = new ResultDAO();
    private final ActivityLogDAO logDAO      = new ActivityLogDAO();

    // ── Dependencies ─────────────────────────────────────────────────────────
    private final MainFrame      mainFrame;
    private final ActivityLogger activityLogger;

    // ── State ────────────────────────────────────────────────────────────────
    private Teacher currentTeacher;

    // ── Inner card names ─────────────────────────────────────────────────────
    private static final String CARD_LOGIN     = "LOGIN";
    private static final String CARD_DASHBOARD = "DASHBOARD";

    private final CardLayout innerCards;
    private final JPanel     innerPanel;

    // ── Login widgets ─────────────────────────────────────────────────────────
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         loginErrorLbl;

    // ── Dashboard widgets ─────────────────────────────────────────────────────
    private JLabel         greetingLbl;
    private DefaultTableModel studentTableModel;
    private JTable           studentTable;
    private DefaultTableModel resultTableModel;
    private JTable           resultTable;

    // ── Student form fields ────────────────────────────────────────────────────
    private JTextField sfRoll, sfName, sfEmail, sfMother;

    // ── Result form fields ─────────────────────────────────────────────────────
    private JTextField rfRoll, rfAnn, rfCnn, rfJava, rfFaiml, rfIot;

    // ─────────────────────────────────────────────────────────────────────────
    public TeacherConsole(MainFrame mainFrame, ActivityLogger activityLogger) {
        this.mainFrame      = mainFrame;
        this.activityLogger = activityLogger;
        this.innerCards     = new CardLayout();
        this.innerPanel     = new JPanel(innerCards);

        setLayout(new BorderLayout());
        setBackground(MainFrame.COLOR_BG);

        add(buildHeader(), BorderLayout.NORTH);
        innerPanel.setBackground(MainFrame.COLOR_BG);
        innerPanel.add(buildLoginPanel(),     CARD_LOGIN);
        innerPanel.add(buildDashboardPanel(), CARD_DASHBOARD);
        add(innerPanel, BorderLayout.CENTER);
    }

    // ═════════════════════════════ HEADER ═══════════════════════════════════
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MainFrame.COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(14, 30, 14, 30));

        JLabel title = new JLabel("👩‍🏫  Teacher Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        greetingLbl = new JLabel();
        greetingLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        greetingLbl.setForeground(new Color(190, 210, 255));

        JButton logoutBtn = smallBtn("Logout");
        logoutBtn.addActionListener(e -> doLogout());

        JButton homeBtn = smallBtn("← Home");
        homeBtn.addActionListener(e -> {
            doLogout();
            mainFrame.showWelcome();
        });

        right.add(greetingLbl);
        right.add(logoutBtn);
        right.add(homeBtn);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ═════════════════════════════ LOGIN ════════════════════════════════════
    private JPanel buildLoginPanel() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(MainFrame.COLOR_BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1, true),
                new EmptyBorder(40, 50, 40, 50)));

        JLabel headLbl = new JLabel("Teacher Login", SwingConstants.CENTER);
        headLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headLbl.setForeground(MainFrame.COLOR_TEXT);
        headLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("Enter your credentials to access the dashboard", SwingConstants.CENTER);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLbl.setForeground(MainFrame.COLOR_SUBTEXT);
        subLbl.setAlignmentX(CENTER_ALIGNMENT);

        usernameField = createFormField("Username");
        passwordField = new JPasswordField();
        styleField(passwordField);

        loginErrorLbl = new JLabel(" ");
        loginErrorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginErrorLbl.setForeground(MainFrame.COLOR_DANGER);
        loginErrorLbl.setAlignmentX(CENTER_ALIGNMENT);

        JButton loginBtn = actionBtn("Login", MainFrame.COLOR_PRIMARY);
        loginBtn.addActionListener(e -> performLogin());

        card.add(headLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(subLbl);
        card.add(Box.createVerticalStrut(30));
        card.add(fLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(fLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(6));
        card.add(loginErrorLbl);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);

        root.add(card);
        return root;
    }

    // ═════════════════════════ DASHBOARD ════════════════════════════════════
    private JPanel buildDashboardPanel() {
        JPanel dash = new JPanel(new BorderLayout());
        dash.setBackground(MainFrame.COLOR_BG);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(MainFrame.COLOR_CARD);

        tabs.addTab("👥  Students",      buildStudentTab());
        tabs.addTab("📊  Results",       buildResultTab());
        tabs.addTab("📈  Reports",       buildReportTab());
        tabs.addTab("📋  Activity Logs", buildLogTab());

        dash.add(tabs, BorderLayout.CENTER);
        return dash;
    }

    // ──────────────────── STUDENTS TAB ──────────────────────────────────────
    private JPanel buildStudentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MainFrame.COLOR_BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // ── Add/Edit Form ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(MainFrame.COLOR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(MainFrame.COLOR_BORDER),
                        " Add / Update Student "),
                new EmptyBorder(8, 12, 8, 12)));

        sfRoll   = new JTextField(15);
        sfName   = new JTextField(15);
        sfEmail  = new JTextField(15);
        sfMother = new JTextField(15);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        addFormRow(form, gc, 0, "Roll Number:", sfRoll);
        addFormRow(form, gc, 1, "Full Name:",   sfName);
        addFormRow(form, gc, 2, "Email:",        sfEmail);
        addFormRow(form, gc, 3, "Mother's Name:",sfMother);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(MainFrame.COLOR_CARD);

        JButton addBtn    = tinyBtn("➕ Add",    MainFrame.COLOR_ACCENT);
        JButton updateBtn = tinyBtn("✏ Update",  MainFrame.COLOR_PRIMARY);
        JButton deleteBtn = tinyBtn("🗑 Delete",  MainFrame.COLOR_DANGER);
        JButton clearBtn  = tinyBtn("✖ Clear",   new Color(120, 120, 120));

        addBtn.addActionListener(e    -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e  -> clearStudentForm());

        btnRow.add(addBtn); btnRow.add(updateBtn);
        btnRow.add(deleteBtn); btnRow.add(clearBtn);

        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 4;
        form.add(btnRow, gc);

        // ── Table ──
        String[] cols = {"ID", "Roll Number", "Name", "Email", "Mother's Name"};
        studentTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        styleTable(studentTable);
        studentTable.setRowSorter(new TableRowSorter<>(studentTableModel));

        // Clicking row populates form
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateStudentForm();
        });

        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.setBackground(MainFrame.COLOR_BG);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setBackground(MainFrame.COLOR_BG);
        JTextField searchField = new JTextField(20);
        JButton searchBtn = tinyBtn("🔍 Search", MainFrame.COLOR_PRIMARY);
        JButton refreshBtn = tinyBtn("⟳ Refresh", new Color(80, 80, 80));
        searchBtn.addActionListener(e -> searchStudents(searchField.getText().trim()));
        refreshBtn.addActionListener(e -> { searchField.setText(""); refreshStudentTable(); });
        searchRow.add(new JLabel("Search name: ")); searchRow.add(searchField);
        searchRow.add(searchBtn); searchRow.add(refreshBtn);

        tablePanel.add(searchRow, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, tablePanel);
        split.setDividerLocation(220);
        split.setResizeWeight(0.3);
        split.setBorder(null);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // ──────────────────── RESULTS TAB ───────────────────────────────────────
    private JPanel buildResultTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MainFrame.COLOR_BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // ── Form ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(MainFrame.COLOR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(MainFrame.COLOR_BORDER),
                        " Add / Update Result  (marks out of 100 each) "),
                new EmptyBorder(8, 12, 8, 12)));

        rfRoll  = new JTextField(12);
        rfAnn   = new JTextField(8);
        rfCnn   = new JTextField(8);
        rfJava  = new JTextField(8);
        rfFaiml = new JTextField(8);
        rfIot   = new JTextField(8);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        addFormRow(form, gc, 0, "Roll Number:", rfRoll);
        addFormRow(form, gc, 1, "ANN Marks:",   rfAnn);
        addFormRow(form, gc, 2, "CNN Marks:",   rfCnn);
        addFormRow(form, gc, 3, "JAVA Marks:",  rfJava);
        addFormRow(form, gc, 4, "FAIML Marks:", rfFaiml);
        addFormRow(form, gc, 5, "IOT Marks:",   rfIot);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(MainFrame.COLOR_CARD);

        JButton addBtn    = tinyBtn("➕ Add / Update", MainFrame.COLOR_ACCENT);
        JButton deleteBtn = tinyBtn("🗑 Delete",       MainFrame.COLOR_DANGER);
        JButton clearBtn  = tinyBtn("✖ Clear",         new Color(120, 120, 120));
        JButton backupBtn = tinyBtn("💾 Backup All",   MainFrame.COLOR_PRIMARY);

        addBtn.addActionListener(e    -> addOrUpdateResult());
        deleteBtn.addActionListener(e -> deleteResult());
        clearBtn.addActionListener(e  -> clearResultForm());
        backupBtn.addActionListener(e -> backupAllResults());

        btnRow.add(addBtn); btnRow.add(deleteBtn);
        btnRow.add(clearBtn); btnRow.add(backupBtn);

        gc.gridx = 0; gc.gridy = 6; gc.gridwidth = 4;
        form.add(btnRow, gc);

        // ── Table ──
        String[] cols = {"Result ID", "Roll Number", "Student Name",
                "ANN", "CNN", "JAVA", "FAIML", "IOT",
                "Total", "Percentage", "Grade", "Status"};
        resultTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(resultTableModel);
        styleTable(resultTable);
        resultTable.setRowSorter(new TableRowSorter<>(resultTableModel));

        resultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateResultFormFromTable();
        });

        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.setBackground(MainFrame.COLOR_BG);
        JButton refreshBtn = tinyBtn("⟳ Refresh", new Color(80, 80, 80));
        refreshBtn.addActionListener(e -> refreshResultTable());
        JPanel tbTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tbTop.setBackground(MainFrame.COLOR_BG);
        tbTop.add(refreshBtn);
        tablePanel.add(tbTop, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, form, tablePanel);
        split.setDividerLocation(270);
        split.setResizeWeight(0.35);
        split.setBorder(null);

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // ──────────────────── REPORTS TAB ───────────────────────────────────────
    private JPanel buildReportTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MainFrame.COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextArea area = new JTextArea();
        area.setFont(new Font("Courier New", Font.PLAIN, 13));
        area.setEditable(false);
        area.setBackground(new Color(30, 39, 46));
        area.setForeground(new Color(223, 230, 233));
        area.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.COLOR_BORDER));

        JButton genBtn = actionBtn("📊  Generate Report", MainFrame.COLOR_PRIMARY);
        genBtn.addActionListener(e -> generateReport(area));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(MainFrame.COLOR_BG);
        top.add(genBtn);

        panel.add(top,    BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void generateReport(JTextArea area) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("   STUDENT RESULT MANAGEMENT REPORT\n");
        sb.append("========================================\n\n");

        int  students      = studentDAO.getStudentCount();
        int  results       = resultDAO.getResultCount();
        int  passed        = resultDAO.getPassCount();
        int  failed        = resultDAO.getFailCount();
        double avgPct      = resultDAO.getAveragePercentage();
        double highPct     = resultDAO.getHighestPercentage();
        double lowPct      = resultDAO.getLowestPercentage();
        double passRate     = resultDAO.getPassPercentage();

        sb.append(String.format("Total Students Registered : %d\n", students));
        sb.append(String.format("Results Entered           : %d\n", results));
        sb.append(String.format("Passed                    : %d\n", passed));
        sb.append(String.format("Failed                    : %d\n", failed));
        sb.append(String.format("Pass Rate                 : %.2f%%\n", passRate));
        sb.append("\n── PERCENTAGE STATS ──────────────────\n");
        sb.append(String.format("Highest Percentage        : %.2f%%\n", highPct));
        sb.append(String.format("Lowest  Percentage        : %.2f%%\n", lowPct));
        sb.append(String.format("Average Percentage        : %.2f%%\n", avgPct));
        sb.append("\n── TOP 10 STUDENTS ───────────────────\n");

        List<Result> top10 = resultDAO.getTopResults(10);
        int rank = 1;
        for (Result r : top10) {
            Student s = studentDAO.getStudentById(r.getStudentId());
            String name = (s != null) ? s.getStudentName() : "Unknown";
            sb.append(String.format("[%2d] %-20s  %.2f%%  %s  %s\n",
                    rank++, name, r.getPercentage(), r.getGrade(), r.getResultStatus()));
        }

        sb.append("\n========================================\n");
        area.setText(sb.toString());
        activityLogger.log(ActivityLogDAO.VIEW_RESULT, "Teacher generated report",
                currentTeacher.getUsername());
    }

    // ──────────────────── ACTIVITY LOGS TAB ─────────────────────────────────
    private JPanel buildLogTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(MainFrame.COLOR_BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        String[] cols = {"Log ID", "Type", "Description", "Performed By", "Timestamp"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);
        table.setRowSorter(new TableRowSorter<>(model));

        JButton refreshBtn = tinyBtn("⟳ Refresh Logs", new Color(80, 80, 80));
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            List<Map<String, Object>> logs = logDAO.getRecentLogs(200);
            for (Map<String, Object> log : logs) {
                model.addRow(new Object[]{
                        log.get("log_id"),
                        log.get("activity_type"),
                        log.get("description"),
                        log.get("performed_by"),
                        log.get("log_timestamp")
                });
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(MainFrame.COLOR_BG);
        top.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ═════════════════════════ STUDENT ACTIONS ═══════════════════════════════
    private void addStudent() {
        String roll   = sfRoll.getText().trim().toUpperCase();
        String name   = sfName.getText().trim();
        String email  = sfEmail.getText().trim();
        String mother = sfMother.getText().trim();

        String err;
        if ((err = ValidationUtil.validateRollNumber(roll))  != null) { showErr(err); return; }
        if ((err = ValidationUtil.validateName(name, "Name")) != null) { showErr(err); return; }
        if ((err = ValidationUtil.validateEmail(email))       != null) { showErr(err); return; }
        if ((err = ValidationUtil.validateName(mother, "Mother's name")) != null) { showErr(err); return; }

        Student s = new Student(roll, name, email, mother, currentTeacher.getTeacherId());
        if (studentDAO.addStudent(s)) {
            showInfo("Student " + roll + " added successfully.");
            activityLogger.log(ActivityLogDAO.ADD_STUDENT,
                    "Added student: " + roll, currentTeacher.getUsername());
            clearStudentForm();
            refreshStudentTable();
        } else {
            showErr("Failed to add student. Roll number may already exist.");
        }
    }

    private void updateStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) { showErr("Select a row in the table to update."); return; }
        int sid = (int) studentTableModel.getValueAt(
                studentTable.convertRowIndexToModel(row), 0);

        Student existing = studentDAO.getStudentById(sid);
        if (existing == null) { showErr("Student not found."); return; }

        existing.setStudentName(sfName.getText().trim());
        existing.setEmail(sfEmail.getText().trim());
        existing.setMotherName(sfMother.getText().trim());

        if (studentDAO.updateStudent(existing)) {
            showInfo("Student updated successfully.");
            activityLogger.log(ActivityLogDAO.UPDATE_STUDENT,
                    "Updated student ID: " + sid, currentTeacher.getUsername());
            refreshStudentTable();
        } else {
            showErr("Failed to update student.");
        }
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) { showErr("Select a row to delete."); return; }
        int sid = (int) studentTableModel.getValueAt(
                studentTable.convertRowIndexToModel(row), 0);
        String rollNum = (String) studentTableModel.getValueAt(
                studentTable.convertRowIndexToModel(row), 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete student " + rollNum + "?\nThis will also delete their result.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        resultDAO.deleteResultByStudentId(sid);
        if (studentDAO.deleteStudent(sid)) {
            showInfo("Student " + rollNum + " deleted.");
            activityLogger.log(ActivityLogDAO.DELETE_STUDENT,
                    "Deleted student: " + rollNum, currentTeacher.getUsername());
            clearStudentForm();
            refreshStudentTable();
            refreshResultTable();
        } else {
            showErr("Failed to delete student.");
        }
    }

    private void searchStudents(String term) {
        if (term.isEmpty()) { refreshStudentTable(); return; }
        List<Student> found = studentDAO.searchStudentsByName(term);
        studentTableModel.setRowCount(0);
        for (Student s : found) populateStudentRow(s);
    }

    private void refreshStudentTable() {
        studentTableModel.setRowCount(0);
        List<Student> list = studentDAO.getAllStudents();
        for (Student s : list) populateStudentRow(s);
    }

    private void populateStudentRow(Student s) {
        studentTableModel.addRow(new Object[]{
                s.getStudentId(), s.getRollNumber(), s.getStudentName(),
                s.getEmail(), s.getMotherName()
        });
    }

    private void populateStudentForm() {
        int row = studentTable.getSelectedRow();
        if (row < 0) return;
        int modelRow = studentTable.convertRowIndexToModel(row);
        sfRoll.setText((String) studentTableModel.getValueAt(modelRow, 1));
        sfName.setText((String) studentTableModel.getValueAt(modelRow, 2));
        sfEmail.setText((String) studentTableModel.getValueAt(modelRow, 3));
        sfMother.setText((String) studentTableModel.getValueAt(modelRow, 4));
    }

    private void clearStudentForm() {
        sfRoll.setText(""); sfName.setText("");
        sfEmail.setText(""); sfMother.setText("");
        studentTable.clearSelection();
    }

    // ═════════════════════════ RESULT ACTIONS ════════════════════════════════
    private void addOrUpdateResult() {
        String roll = rfRoll.getText().trim().toUpperCase();
        if (roll.isEmpty()) { showErr("Enter a roll number."); return; }

        Student s = studentDAO.getStudentByRollNumber(roll);
        if (s == null) { showErr("Student with roll number " + roll + " not found."); return; }

        double ann, cnn, java, faiml, iot;
        try {
            ann   = Double.parseDouble(rfAnn.getText().trim());
            cnn   = Double.parseDouble(rfCnn.getText().trim());
            java  = Double.parseDouble(rfJava.getText().trim());
            faiml = Double.parseDouble(rfFaiml.getText().trim());
            iot   = Double.parseDouble(rfIot.getText().trim());
        } catch (NumberFormatException ex) {
            showErr("All marks must be valid numbers."); return;
        }

        if (!GradeCalculator.areAllMarksValid(ann, cnn, java, faiml, iot)) {
            showErr("Marks must be between 0 and 100."); return;
        }

        Result result = new Result(s.getStudentId(), ann, cnn, java, faiml, iot);

        boolean success;
        String action;
        if (resultDAO.isResultExists(s.getStudentId())) {
            success = resultDAO.updateResult(result);
            action  = ActivityLogDAO.UPDATE_RESULT;
        } else {
            success = resultDAO.addResult(result);
            action  = ActivityLogDAO.ADD_RESULT;
        }

        if (success) {
            showInfo("Result saved for " + roll
                    + "  |  Total: " + result.getTotalMarks()
                    + "  |  " + result.getGrade() + "  |  " + result.getResultStatus());
            activityLogger.log(action, "Result for " + roll, currentTeacher.getUsername());
            clearResultForm();
            refreshResultTable();
        } else {
            showErr("Failed to save result.");
        }
    }

    private void deleteResult() {
        int row = resultTable.getSelectedRow();
        if (row < 0) { showErr("Select a row to delete."); return; }
        int modelRow = resultTable.convertRowIndexToModel(row);
        int resultId = (int) resultTableModel.getValueAt(modelRow, 0);
        String rollNum = (String) resultTableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete result for " + rollNum + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (resultDAO.deleteResult(resultId)) {
            showInfo("Result deleted.");
            activityLogger.log(ActivityLogDAO.DELETE_RESULT,
                    "Deleted result for " + rollNum, currentTeacher.getUsername());
            clearResultForm();
            refreshResultTable();
        } else {
            showErr("Failed to delete result.");
        }
    }

    private void backupAllResults() {
        List<Student> students = studentDAO.getAllStudents();
        java.util.List<FileHandler.StudentResultPair> pairs = new java.util.ArrayList<>();
        for (Student s : students) {
            Result r = resultDAO.getResultByStudentId(s.getStudentId());
            if (r != null) pairs.add(new FileHandler.StudentResultPair(s, r));
        }

        if (pairs.isEmpty()) { showErr("No results to backup."); return; }

        String path = FileHandler.backupMultipleResultsToTextFile(pairs, null);
        if (path != null) {
            showInfo("Backup saved to: " + path);
            activityLogger.log(ActivityLogDAO.FILE_BACKUP,
                    "Full backup created: " + path, currentTeacher.getUsername());
        } else {
            showErr("Backup failed.");
        }
    }

    private void refreshResultTable() {
        resultTableModel.setRowCount(0);
        List<Result> results = resultDAO.getAllResults();
        for (Result r : results) {
            Student s = studentDAO.getStudentById(r.getStudentId());
            String roll = (s != null) ? s.getRollNumber()  : "N/A";
            String name = (s != null) ? s.getStudentName() : "N/A";
            resultTableModel.addRow(new Object[]{
                    r.getResultId(), roll, name,
                    r.getANN_marks(), r.getCNN_marks(), r.getJAVA_marks(),
                    r.getFAIML_marks(), r.getIOT_marks(),
                    r.getTotalMarks(),
                    String.format("%.2f%%", r.getPercentage()),
                    r.getGrade(), r.getResultStatus()
            });
        }
    }

    private void populateResultFormFromTable() {
        int row = resultTable.getSelectedRow();
        if (row < 0) return;
        int modelRow = resultTable.convertRowIndexToModel(row);
        rfRoll.setText((String) resultTableModel.getValueAt(modelRow, 1));
        rfAnn.setText(String.valueOf(resultTableModel.getValueAt(modelRow, 3)));
        rfCnn.setText(String.valueOf(resultTableModel.getValueAt(modelRow, 4)));
        rfJava.setText(String.valueOf(resultTableModel.getValueAt(modelRow, 5)));
        rfFaiml.setText(String.valueOf(resultTableModel.getValueAt(modelRow, 6)));
        rfIot.setText(String.valueOf(resultTableModel.getValueAt(modelRow, 7)));
    }

    private void clearResultForm() {
        rfRoll.setText(""); rfAnn.setText("");
        rfCnn.setText(""); rfJava.setText("");
        rfFaiml.setText(""); rfIot.setText("");
        resultTable.clearSelection();
    }

    // ═════════════════════════ AUTH ══════════════════════════════════════════
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            loginErrorLbl.setText("Username and password are required.");
            return;
        }

        Teacher teacher = teacherDAO.authenticateTeacher(username, password);
        if (teacher == null) {
            loginErrorLbl.setText("Invalid username or password.");
            activityLogger.log(ActivityLogDAO.LOGIN,
                    "Failed login attempt: " + username, username);
            return;
        }

        currentTeacher = teacher;
        greetingLbl.setText("👤  " + teacher.getFullName() + "  |  ");
        activityLogger.log(ActivityLogDAO.LOGIN,
                "Teacher logged in: " + username, username);

        refreshStudentTable();
        refreshResultTable();
        innerCards.show(innerPanel, CARD_DASHBOARD);
    }

    private void doLogout() {
        if (currentTeacher != null) {
            activityLogger.log(ActivityLogDAO.LOGOUT,
                    "Teacher logged out: " + currentTeacher.getUsername(),
                    currentTeacher.getUsername());
            currentTeacher = null;
        }
        greetingLbl.setText("");
        usernameField.setText("");
        passwordField.setText("");
        loginErrorLbl.setText(" ");
        innerCards.show(innerPanel, CARD_LOGIN);
    }

    /** Called by MainFrame when the card is made visible again. */
    public void reset() {
        if (currentTeacher == null) {
            innerCards.show(innerPanel, CARD_LOGIN);
        } else {
            innerCards.show(innerPanel, CARD_DASHBOARD);
        }
    }

    // ═════════════════════════ UI HELPERS ════════════════════════════════════
    private void addFormRow(JPanel p, GridBagConstraints gc, int row,
                            String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        p.add(new JLabel(label), gc);
        gc.gridx = 1; gc.gridwidth = 3;
        field.setPreferredSize(new Dimension(240, 28));
        p.add(field, gc);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(MainFrame.COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(210, 225, 255));
        table.setGridColor(MainFrame.COLOR_BORDER);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private JTextField createFormField(String ph) {
        JTextField tf = new JTextField();
        styleField(tf);
        return tf;
    }

    private void styleField(JComponent c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.setMaximumSize(new Dimension(340, 38));
        c.setPreferredSize(new Dimension(340, 38));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.COLOR_BORDER),
                new EmptyBorder(4, 10, 4, 10)));
    }

    private JLabel fLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(MainFrame.COLOR_TEXT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JButton actionBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(340, 44));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JButton tinyBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JButton smallBtn(String text) {
        return tinyBtn(text, new Color(60, 80, 180));
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErr(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
