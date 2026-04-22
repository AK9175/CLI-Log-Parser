package com.logparser.aggregator;

import com.logparser.entry.LogEntry;

public interface LogAggregator {
    void add(LogEntry entry);
    Object getResult();
    String getOutputFileName();
}
