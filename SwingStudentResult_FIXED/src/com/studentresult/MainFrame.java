package com.studentresult;

import com.studentresult.console.StudentConsole;
import com.studentresult.console.TeacherConsole;
import com.studentresult.thread.ActivityLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Root application window.
 * Uses a CardLayout so that WelcomePanel, TeacherConsole and StudentConsole
 * can be swapped in/out without opening new windows.
 */
public class MainFrame extends JFrame {

    // ── Card names ──────────────────────────────────────────────────────────
    public static final String CARD_WELCOME  = "WELCOME";
    public static final String CARD_TEACHER  = "TEACHER";
    public static final String CARD_STUDENT  = "STUDENT";

    // ── Theme colours ───────────────────────────────────────────────────────
    public static final Color COLOR_BG        = new Color(245, 247, 250);
    public static final Color COLOR_PRIMARY   = new Color(41, 98, 255);
    public static final Color COLOR_PRIMARY_D = new Color(25, 70, 200);
    public static final Color COLOR_ACCENT    = new Color(0, 184, 148);
    public static final Color COLOR_DANGER    = new Color(231, 76, 60);
    public static final Color COLOR_TEXT      = new Color(30, 39, 46);
    public static final Color COLOR_SUBTEXT   = new Color(99, 110, 114);
    public static final Color COLOR_CARD      = Color.WHITE;
    public static final Color COLOR_BORDER    = new Color(220, 221, 226);

    // ── State ───────────────────────────────────────────────────────────────
    private final ActivityLogger activityLogger;
    private final CardLayout     cardLayout;
    private final JPanel         cardPanel;

    private TeacherConsole teacherConsole;
    private StudentConsole studentConsole;

    // ───────────────────────────────────────────────────────────────────────
    public MainFrame(ActivityLogger activityLogger) {
        this.activityLogger = activityLogger;
        this.cardLayout     = new CardLayout();
        this.cardPanel      = new JPanel(cardLayout);

        configureFrame();
        buildCards();
    }

    // ── Frame setup ─────────────────────────────────────────────────────────
    private void configureFrame() {
        setTitle("Student Result Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());

        cardPanel.setBackground(COLOR_BG);
        setContentPane(cardPanel);
    }

    private Image createAppIcon() {
        // Simple 32×32 blue square as placeholder icon
        java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(32, 32,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(COLOR_PRIMARY);
        g.fillRoundRect(0, 0, 32, 32, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("S", 9, 23);
        g.dispose();
        return img;
    }

    // ── Card construction ───────────────────────────────────────────────────
    private void buildCards() {
        cardPanel.add(buildWelcomeCard(), CARD_WELCOME);

        // Teacher and Student consoles are created lazily to avoid DB hit
        // before the welcome screen is shown.
        showCard(CARD_WELCOME);
    }

    private JPanel buildWelcomeCard() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);

        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLbl = new JLabel("STUDENT RESULT MANAGEMENT SYSTEM");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subLbl = new JLabel("Academic Performance Tracker  ·  Version 2.0  ·  Swing + JDBC");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLbl.setForeground(new Color(190, 210, 255));
        subLbl.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(titleLbl, BorderLayout.CENTER);
        header.add(subLbl,   BorderLayout.SOUTH);

        // ── Centre card ─────────────────────────────────────────────────────
        JPanel centre = new JPanel(new GridBagLayout());
        centre.setBackground(COLOR_BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(50, 60, 50, 60)));

        // Icon
        JLabel iconLbl = new JLabel("🎓", SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLbl = new JLabel("Welcome", SwingConstants.CENTER);
        welcomeLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLbl.setForeground(COLOR_TEXT);
        welcomeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instrLbl = new JLabel("Please choose your portal to continue",
                SwingConstants.CENTER);
        instrLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instrLbl.setForeground(COLOR_SUBTEXT);
        instrLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JButton teacherBtn = createWelcomeButton("👩‍🏫  Teacher Portal",
                "Login to manage students and results", COLOR_PRIMARY);
        teacherBtn.addActionListener(e -> openTeacherPortal());

        JButton studentBtn = createWelcomeButton("🎓  Student Portal",
                "View your academic result card", COLOR_ACCENT);
        studentBtn.addActionListener(e -> openStudentPortal());

        JButton exitBtn = createWelcomeButton("✖  Exit Application",
                "Close the application", COLOR_DANGER);
        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?", "Confirm Exit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(12));
        card.add(welcomeLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(instrLbl);
        card.add(Box.createVerticalStrut(40));
        card.add(teacherBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(studentBtn);
        card.add(Box.createVerticalStrut(14));
        card.add(exitBtn);

        centre.add(card);

        // ── Footer ──────────────────────────────────────────────────────────
        JLabel footer = new JLabel(
                "© 2026 Student Result Management System  |  Subjects: ANN · CNN · JAVA · FAIML · IOT",
                SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(COLOR_SUBTEXT);
        footer.setBorder(new EmptyBorder(10, 0, 10, 0));

        root.add(header, BorderLayout.NORTH);
        root.add(centre, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        return root;
    }

    private JButton createWelcomeButton(String text, String tooltip, Color bg) {
        JButton btn = new JButton(text);
        btn.setToolTipText(tooltip);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(320, 48));
        btn.setPreferredSize(new Dimension(320, 48));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color original = bg;
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(original.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(original);
            }
        });
        return btn;
    }

    // ── Navigation ──────────────────────────────────────────────────────────
    public void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    public void showWelcome() {
        showCard(CARD_WELCOME);
    }

    private void openTeacherPortal() {
        // Lazy-initialise teacher console
        if (teacherConsole == null) {
            teacherConsole = new TeacherConsole(this, activityLogger);
            cardPanel.add(teacherConsole, CARD_TEACHER);
        }
        teacherConsole.reset();
        showCard(CARD_TEACHER);
    }

    private void openStudentPortal() {
        // Lazy-initialise student console
        if (studentConsole == null) {
            studentConsole = new StudentConsole(this, activityLogger);
            cardPanel.add(studentConsole, CARD_STUDENT);
        }
        studentConsole.reset();
        showCard(CARD_STUDENT);
    }
}
