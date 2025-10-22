package com.happyfeet.service.impl;

import com.happyfeet.service.LoggerManager;

public class LoggerServiceImpl implements LoggerManager {
    @Override
    public void logInfo(String message) {
        System.out.println("INFO: " + message);
    }

    @Override
    public void logError(String errorMessage) {
        System.err.println("ERROR: " + errorMessage);
    }

    @Override
    public void logWarning(String warningMessage) {
        System.out.println("WARNING: " + warningMessage);
    }

    @Override
    public void log(String message) {
        // Route generic log to info for now
        logInfo(message);
    }
}
