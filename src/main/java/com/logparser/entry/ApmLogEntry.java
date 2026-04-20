package com.logparser.entry;

public class ApmLogEntry extends LogEntry {
    private final String metric;
    private final double value;

    public ApmLogEntry(String timestamp, String host, String metric, double value) {
        super(timestamp, host);
        this.metric = metric;
        this.value = value;
    }

    public String getMetric() { return metric; }
    public double getValue() { return value; }
}
