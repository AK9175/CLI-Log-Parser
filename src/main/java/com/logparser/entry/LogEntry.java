package com.logparser.entry;

public abstract class LogEntry {
    private final String timestamp;
    private final String host;

    protected LogEntry(String timestamp, String host) {
        this.timestamp = timestamp;
        this.host = host;
    }

    public String getTimestamp() { return timestamp; }
    public String getHost() { return host; }
}
