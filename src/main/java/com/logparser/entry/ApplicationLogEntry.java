package com.logparser.entry;

public class ApplicationLogEntry extends LogEntry {
    private final String level;
    private final String message;

    public ApplicationLogEntry(String timestamp, String host, String level, String message) {
        super(timestamp, host);
        this.level = level;
        this.message = message;
    }

    public String getLevel() { return level; }
    public String getMessage() { return message; }
}
