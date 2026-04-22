package com.logparser.aggregator;

import com.logparser.entry.ApplicationLogEntry;
import com.logparser.entry.LogEntry;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationLogAggregator implements LogAggregator {
    private final Map<String, Integer> countByLevel = new LinkedHashMap<>();

    @Override
    public void add(LogEntry entry) {
        ApplicationLogEntry app = (ApplicationLogEntry) entry;
        countByLevel.merge(app.getLevel().toUpperCase(), 1, Integer::sum);
    }

    @Override
    public Map<String, Integer> getResult() {
        return new LinkedHashMap<>(countByLevel);
    }

    @Override
    public String getOutputFileName() { return "application.json"; }
}
