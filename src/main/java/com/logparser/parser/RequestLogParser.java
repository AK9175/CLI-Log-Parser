package com.logparser.parser;

import com.logparser.entry.LogEntry;
import com.logparser.entry.RequestLogEntry;
import java.util.Map;

public class RequestLogParser implements LogParser {

    @Override
    public boolean canParse(Map<String, String> fields) {
        return fields.containsKey("request_method")
            && fields.containsKey("request_url")
            && fields.containsKey("response_status")
            && fields.containsKey("response_time_ms");
    }

    @Override
    public LogEntry parse(Map<String, String> fields) {
        String timestamp = fields.getOrDefault("timestamp", "");
        String host = fields.getOrDefault("host", "");
        String method = fields.get("request_method");
        String url = fields.get("request_url");
        int statusCode = Integer.parseInt(fields.get("response_status"));
        int responseTimeMs = Integer.parseInt(fields.get("response_time_ms"));
        return new RequestLogEntry(timestamp, host, method, url, statusCode, responseTimeMs);
    }
}
