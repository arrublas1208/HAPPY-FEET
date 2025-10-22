package com.happyfeet.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger mínimo que escribe en un archivo de texto en el directorio de trabajo.
 * Archivo por defecto: happyfeet.log
 */
public class FileLogger {
    private static final String DEFAULT_LOG_FILE = System.getProperty("happyfeet.log", "happyfeet.log");
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final FileLogger INSTANCE = new FileLogger();

    private final Object lock = new Object();
    private final File logFile;

    public static FileLogger getInstance() {
        return INSTANCE;
    }

    private FileLogger() {
        this.logFile = new File(DEFAULT_LOG_FILE);
        try {
            ensureParent(this.logFile);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("[FileLogger] No se pudo inicializar el archivo de log: " + e.getMessage());
        }
    }

    private void ensureParent(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public void info(String msg) {
        write("INFO", msg, null);
    }

    public void warn(String msg) {
        write("WARN", msg, null);
    }

    public void error(String msg) {
        write("ERROR", msg, null);
    }

    public void error(String msg, Throwable t) {
        write("ERROR", msg, t);
    }

    private void write(String level, String msg, Throwable t) {
        String ts = LocalDateTime.now().format(TS);
        String line = String.format("%s [%s] %s", ts, level, msg);
        synchronized (lock) {
            try (PrintWriter out = new PrintWriter(new FileWriter(logFile, StandardCharsets.UTF_8, true))) {
                out.println(line);
                if (t != null) {
                    t.printStackTrace(out);
                }
            } catch (IOException e) {
                System.err.println("[FileLogger] Error escribiendo log: " + e.getMessage());
                System.err.println(line);
                if (t != null) t.printStackTrace();
            }
        }
    }
}
