package com.studentresult;

import com.studentresult.thread.ActivityLogger;
import com.studentresult.dao.ActivityLogDAO;
import com.studentresult.util.FileHandler;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // ── Backend initialisation ──────────────────────────────────────────
        FileHandler.initializeDirectories();

        ActivityLogger activityLogger = ActivityLogger.getInstance();
        activityLogger.start();
        activityLogger.log(ActivityLogDAO.SYSTEM_START,
                "Student Result Management System started", "SYSTEM");

        // ── Graceful shutdown hook ──────────────────────────────────────────
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            activityLogger.log(ActivityLogDAO.SYSTEM_SHUTDOWN,
                    "Student Result Management System shutdown", "SYSTEM");
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
            activityLogger.stop();
        }));

        // ── Launch Swing UI on the Event Dispatch Thread ────────────────────
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            MainFrame frame = new MainFrame(activityLogger);
            frame.setVisible(true);
        });
    }
}
