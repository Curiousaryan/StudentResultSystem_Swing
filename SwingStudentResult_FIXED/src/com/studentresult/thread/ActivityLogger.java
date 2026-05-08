package com.studentresult.thread;

import com.studentresult.dao.ActivityLogDAO;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActivityLogger implements Runnable {
    private static ActivityLogger instance;
    private Thread loggerThread;
    private BlockingQueue<LogEntry> logQueue;
    private ActivityLogDAO activityLogDAO;
    private volatile boolean running;
    
    private static final LogEntry POISON_PILL = 
        new LogEntry("SHUTDOWN", "Logger shutdown", "SYSTEM");

    private ActivityLogger() {
        this.logQueue = new LinkedBlockingQueue<>();
        this.activityLogDAO = new ActivityLogDAO();
        this.running = false;
    }
    

    public static synchronized ActivityLogger getInstance() {
        if (instance == null) {
            instance = new ActivityLogger();
        }
        return instance;
    }

    public synchronized void start() {
        if (!running) {
            running = true;
            loggerThread = new Thread(this, "ActivityLoggerThread");
            loggerThread.setDaemon(true); // Daemon thread - won't prevent JVM shutdown
            loggerThread.start();
            System.out.println("ActivityLogger thread started");
        } else {
            System.out.println("ActivityLogger is already running");
        }
    }

    public synchronized void stop() {
        if (running) {
            running = false;
            
            try {

                logQueue.put(POISON_PILL);
                

                if (loggerThread != null) {
                    loggerThread.join(5000);
                }
                

                activityLogDAO.close();
                
                System.out.println("ActivityLogger thread stopped");
            } catch (InterruptedException e) {
                System.err.println("Error stopping ActivityLogger: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void log(String activityType, String description, String performedBy) {
        if (!running) {
            System.err.println("ActivityLogger is not running. Starting it now...");
            start();
        }
        
        try {
            LogEntry entry = new LogEntry(activityType, description, performedBy);
            logQueue.put(entry); // Blocks if queue is full (unlikely)
            

            
        } catch (InterruptedException e) {
            System.err.println("Error adding log to queue: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void log(String activityType, String description) {
        log(activityType, description, "SYSTEM");
    }

    @Override
    public void run() {
        System.out.println("ActivityLogger thread is now running...");
        
        while (running) {
            try {
                LogEntry entry = logQueue.take();
                if (entry == POISON_PILL) {
                    System.out.println("ActivityLogger received shutdown signal");
                    break;
                }
                
                boolean success = activityLogDAO.logActivity(
                    entry.getActivityType(),
                    entry.getDescription(),
                    entry.getPerformedBy()
                );
                
                if (!success) {
                    System.err.println("Failed to log activity: " + entry.getActivityType());
                }
                
            } catch (InterruptedException e) {
                System.err.println("ActivityLogger thread interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
                
            } catch (Exception e) {
                System.err.println("Error in ActivityLogger thread: " + e.getMessage());
                e.printStackTrace();

            }
        }
        
        System.out.println("ActivityLogger thread terminated");
    }

    public int getPendingLogsCount() {
        return logQueue.size();
    }

    public void clearQueue() {
        logQueue.clear();
        System.out.println("ActivityLogger queue cleared");
    }

    private static class LogEntry {
        private String activityType;
        private String description;
        private String performedBy;
        
        public LogEntry(String activityType, String description, String performedBy) {
            this.activityType = activityType;
            this.description = description;
            this.performedBy = performedBy;
        }
        
        public String getActivityType() {
            return activityType;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getPerformedBy() {
            return performedBy;
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("ACTIVITY LOGGER THREAD TEST");
        System.out.println("========================================");
        System.out.println();
        ActivityLogger logger = ActivityLogger.getInstance();
        
        // Start the logger thread
        logger.start();

        System.out.println("Logging test activities...");
        logger.log(ActivityLogDAO.LOGIN, "Teacher admin logged in", "admin");
        logger.log(ActivityLogDAO.ADD_STUDENT, "Student T400810438 added", "admin");
        logger.log(ActivityLogDAO.ADD_RESULT, "Result added for student T400810438", "admin");
        logger.log(ActivityLogDAO.VIEW_RESULT, "Student viewed result", "T400810438");
        logger.log(ActivityLogDAO.LOGOUT, "Teacher admin logged out", "admin");
        
        System.out.println("✓ 5 activities queued for logging");
        System.out.println("Pending logs: " + logger.getPendingLogsCount());
        System.out.println();
        

        System.out.println("Waiting for logs to be processed...");
        try {
            Thread.sleep(3000); // Wait 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("Remaining pending logs: " + logger.getPendingLogsCount());
        System.out.println();
        

        logger.stop();
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("TEST COMPLETED");
        System.out.println("Check activity_log table in database");
        System.out.println("========================================");
    }
}
