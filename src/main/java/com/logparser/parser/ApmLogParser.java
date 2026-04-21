package com.logparser.parser;

import com.logparser.entry.ApmLogEntry;
import com.logparser.entry.LogEntry;
import java.util.Map;

public class ApmLogParser implements LogParser {

    @Override
    public boolean canParse(Map<String, String> fields) {
        return fields.containsKey("metric") && fields.containsKey("value");
    }

    @Override
    public LogEntry parse(Map<String, String> fields) {
        String timestamp = fields.getOrDefault("timestamp", "");
        String host = fields.getOrDefault("host", "");
        String metric = fields.get("metric");
        double value = Double.parseDouble(fields.get("value"));
        return new ApmLogEntry(timestamp, host, metric, value);
    }
}
