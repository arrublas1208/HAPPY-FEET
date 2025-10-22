package com.happyfeet.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class AppLogger {
    private static final String LOG_FILE = "happyfeet.log";
    public static synchronized void log(String level, String msg) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            pw.printf("%s %-5s %s%n", LocalDateTime.now(), level, msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void info(String msg) { log("INFO", msg); }
    public static void warn(String msg) { log("WARN", msg); }
    public static void error(String msg) { log("ERROR", msg); }
}
