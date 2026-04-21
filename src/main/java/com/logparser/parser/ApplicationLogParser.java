package com.logparser.parser;

import com.logparser.entry.ApplicationLogEntry;
import com.logparser.entry.LogEntry;
import java.util.Map;

public class ApplicationLogParser implements LogParser {

    @Override
    public boolean canParse(Map<String, String> fields) {
        return fields.containsKey("level");
    }

    @Override
    public LogEntry parse(Map<String, String> fields) {
        String timestamp = fields.getOrDefault("timestamp", "");
        String host = fields.getOrDefault("host", "");
        String level = fields.get("level");
        String message = fields.getOrDefault("message", "");
        return new ApplicationLogEntry(timestamp, host, level, message);
    }
}
