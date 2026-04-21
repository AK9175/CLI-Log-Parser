package com.logparser.parser;

import com.logparser.entry.LogEntry;
import java.util.Map;

public interface LogParser {
    boolean canParse(Map<String, String> fields);
    LogEntry parse(Map<String, String> fields);
}
