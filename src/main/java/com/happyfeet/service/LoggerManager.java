package com.happyfeet.service;

/**
 * Simple logging interface for the application, using consistent camelCase names.
 */
public interface LoggerManager {
    /** Log a generic informational message. */
    void logInfo(String message);

    /** Log an error message. */
    void logError(String errorMessage);

    /** Log a warning message. */
    void logWarning(String warningMessage);

    /** Log a generic message (alias, routed as info by default). */
    void log(String message);
}
