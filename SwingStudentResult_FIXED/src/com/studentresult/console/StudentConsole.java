package com.studentresult.console;

import com.studentresult.MainFrame;
import com.studentresult.dao.ActivityLogDAO;
import com.studentresult.dao.ResultDAO;
import com.studentresult.dao.StudentDAO;
import com.studentresult.model.Result;
import com.studentresult.model.Student;
import com.studentresult.thread.ActivityLogger;
import com.studentresult.util.FileHandler;
import com.studentresult.util.GradeCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Student portal panel.
 * Students authenticate with Roll Number + Mother's Name to view their result.
 */
public class StudentConsole extends JPanel {

    private final MainFrame mainFrame;
    private final ActivityLogger activityLogger;
    private final StudentDAO studentDAO = new StudentDAO();
    private final ResultDAO resultDAO = new ResultDAO();

    // ── Login widgets ───────────────────────────────────────────────────────
    private JTextField rollField;
    private JPasswordField motherField;
    private JLabel loginErrorLbl;

    // ── Result widgets ──────────────────────────────────────────────────────
    private JPanel resultArea;

    // ── Card names ──────────────────────────────────────────────────────────
    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_RESULT = "RESULT";

    private final CardLayout innerCards;
    private final JPanel innerPanel;

    // ───────────────────────────────────────────────────────────────────────
    public StudentConsole(MainFrame mainFrame, ActivityLogger activityLogger) {

        this.mainFrame = mainFrame;
        this.activityLogger = activityLogger;

        this.innerCards = new CardLayout();
        this.innerPanel = new JPanel(innerCards);

        setLayout(new BorderLayout());
        setBackground(MainFrame.COLOR_BG);

        add(buildHeader(), BorderLayout.NORTH);

        innerPanel.setBackground(MainFrame.COLOR_BG);

        innerPanel.add(buildLoginPanel(), CARD_LOGIN);
        innerPanel.add(buildResultPanel(), CARD_RESULT);

        add(innerPanel, BorderLayout.CENTER);
    }

    // ── Header ──────────────────────────────────────────────────────────────
    private JPanel buildHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(MainFrame.COLOR_ACCENT);

        header.setBorder(new EmptyBorder(16, 30, 16, 30));

        JLabel title = new JLabel("🎓 Student Portal");

        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        title.setForeground(Color.WHITE);

        JButton backBtn = new JButton("← Back to Home");

        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        backBtn.setForeground(Color.WHITE);

        backBtn.setBackground(new Color(0, 150, 120));

        backBtn.setBorderPainted(false);

        backBtn.setFocusPainted(false);

        backBtn.setOpaque(true);

        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        backBtn.addActionListener(e -> mainFrame.showWelcome());

        header.add(title, BorderLayout.WEST);

        header.add(backBtn, BorderLayout.EAST);

        return header;
    }

    // ── Login Panel ─────────────────────────────────────────────────────────
    private JPanel buildLoginPanel() {

        JPanel root = new JPanel(new GridBagLayout());

        root.setBackground(MainFrame.COLOR_BG);

        JPanel card = new JPanel();

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setBackground(MainFrame.COLOR_CARD);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1, true),
                new EmptyBorder(40, 50, 40, 50)));

        JLabel headLbl = new JLabel("Verify Your Identity", SwingConstants.CENTER);

        headLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));

        headLbl.setForeground(MainFrame.COLOR_TEXT);

        headLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel(
                "Enter your credentials to view result",
                SwingConstants.CENTER);

        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        subLbl.setForeground(MainFrame.COLOR_SUBTEXT);

        subLbl.setAlignmentX(CENTER_ALIGNMENT);

        // Roll Number Field
        rollField = createField("e.g. T400810401");

        // Mother's Name Field
        motherField = new JPasswordField();

        motherField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        motherField.setMaximumSize(new Dimension(340, 38));

        motherField.setPreferredSize(new Dimension(340, 38));

        motherField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.COLOR_BORDER),
                new EmptyBorder(4, 10, 4, 10)));

        // Error Label
        loginErrorLbl = new JLabel(" ");

        loginErrorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        loginErrorLbl.setForeground(MainFrame.COLOR_DANGER);

        loginErrorLbl.setAlignmentX(CENTER_ALIGNMENT);

        // Login Button
        JButton viewBtn = makeButton(
                "View My Result",
                MainFrame.COLOR_ACCENT);

        viewBtn.addActionListener(e -> performStudentLogin());

        // SAFE Enter key setup
        SwingUtilities.invokeLater(() -> {

            if (getRootPane() != null) {

                getRootPane().setDefaultButton(viewBtn);
            }
        });

        // Build UI
        card.add(headLbl);

        card.add(Box.createVerticalStrut(6));

        card.add(subLbl);

        card.add(Box.createVerticalStrut(30));

        card.add(fieldLabel("Roll Number"));

        card.add(Box.createVerticalStrut(4));

        card.add(rollField);

        card.add(Box.createVerticalStrut(16));

        card.add(fieldLabel("Mother's Name"));

        card.add(Box.createVerticalStrut(4));

        card.add(motherField);

        card.add(Box.createVerticalStrut(8));

        card.add(loginErrorLbl);

        card.add(Box.createVerticalStrut(20));

        card.add(viewBtn);

        root.add(card);

        return root;
    }

    // ── Result Panel ─────────────────────────────────────────────────────────
    private JPanel buildResultPanel() {

        JPanel root = new JPanel(new BorderLayout());

        root.setBackground(MainFrame.COLOR_BG);

        JScrollPane scroll = new JScrollPane();

        scroll.setBorder(null);

        scroll.setBackground(MainFrame.COLOR_BG);

        scroll.getViewport().setBackground(MainFrame.COLOR_BG);

        resultArea = new JPanel();

        resultArea.setBackground(MainFrame.COLOR_BG);

        resultArea.setLayout(new BoxLayout(resultArea, BoxLayout.Y_AXIS));

        scroll.setViewportView(resultArea);

        root.add(scroll, BorderLayout.CENTER);

        return root;
    }

    // ── Actions ──────────────────────────────────────────────────────────────
    private void performStudentLogin() {

        String rollNumber = rollField.getText().trim().toUpperCase();

        String motherName = new String(motherField.getPassword()).trim();

        if (rollNumber.isEmpty() || motherName.isEmpty()) {

            showLoginError("Please fill in all fields.");

            return;
        }

        Student student = studentDAO.authenticateStudent(
                rollNumber,
                motherName);

        if (student == null) {

            showLoginError(
                    "Invalid roll number or mother's name. Please try again.");

            activityLogger.log(
                    ActivityLogDAO.VIEW_RESULT,
                    "Failed student login attempt: " + rollNumber,
                    rollNumber);

            return;
        }

        Result result = resultDAO.getResultByStudentId(
                student.getStudentId());

        activityLogger.log(
                ActivityLogDAO.VIEW_RESULT,
                "Student viewed result: " + rollNumber,
                rollNumber);

        populateResultArea(student, result);

        innerCards.show(innerPanel, CARD_RESULT);
    }

    private void populateResultArea(Student student, Result result) {

        resultArea.removeAll();

        resultArea.add(Box.createVerticalStrut(20));

        JPanel centre = new JPanel(new GridBagLayout());

        centre.setBackground(MainFrame.COLOR_BG);

        JPanel card = new JPanel();

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setBackground(MainFrame.COLOR_CARD);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.COLOR_BORDER, 1, true),
                new EmptyBorder(30, 40, 30, 40)));

        JLabel title = new JLabel("📄 Result Card", SwingConstants.CENTER);

        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        title.setForeground(MainFrame.COLOR_PRIMARY);

        title.setAlignmentX(CENTER_ALIGNMENT);

        card.add(title);

        centre.add(card);

        resultArea.add(centre);

        resultArea.revalidate();

        resultArea.repaint();
    }

    private void saveResultToFile(Student student, Result result) {

        String path = FileHandler.backupResultToTextFile(student, result);

        if (path != null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Result saved to:\n" + path,
                    "File Saved",
                    JOptionPane.INFORMATION_MESSAGE);

            activityLogger.log(
                    ActivityLogDAO.FILE_BACKUP,
                    "Result file saved for: " + student.getRollNumber(),
                    student.getRollNumber());

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save result to file.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private void showLoginError(String msg) {

        loginErrorLbl.setText(msg);
    }

    public void reset() {

        if (rollField != null) {

            rollField.setText("");
        }

        if (motherField != null) {

            motherField.setText("");
        }

        if (loginErrorLbl != null) {

            loginErrorLbl.setText(" ");
        }

        innerCards.show(innerPanel, CARD_LOGIN);
    }

    private JTextField createField(String placeholder) {

        JTextField tf = new JTextField();

        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tf.setMaximumSize(new Dimension(340, 38));

        tf.setPreferredSize(new Dimension(340, 38));

        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.COLOR_BORDER),
                new EmptyBorder(4, 10, 4, 10)));

        return tf;
    }

    private JLabel fieldLabel(String text) {

        JLabel lbl = new JLabel(text);

        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lbl.setForeground(MainFrame.COLOR_TEXT);

        lbl.setAlignmentX(LEFT_ALIGNMENT);

        return lbl;
    }

    private JButton makeButton(String text, Color bg) {

        JButton btn = new JButton(text);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btn.setForeground(Color.WHITE);

        btn.setBackground(bg);

        btn.setBorderPainted(false);

        btn.setFocusPainted(false);

        btn.setOpaque(true);

        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.setMaximumSize(new Dimension(340, 42));

        btn.setAlignmentX(CENTER_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent e) {

                btn.setBackground(bg.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {

                btn.setBackground(bg);
            }
        });

        return btn;
    }
}