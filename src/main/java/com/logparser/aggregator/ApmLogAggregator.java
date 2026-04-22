package com.logparser.aggregator;

import com.logparser.entry.ApmLogEntry;
import com.logparser.entry.LogEntry;
import java.util.*;

public class ApmLogAggregator implements LogAggregator {
    private final Map<String, List<Double>> valuesByMetric = new LinkedHashMap<>();

    @Override
    public void add(LogEntry entry) {
        ApmLogEntry apm = (ApmLogEntry) entry;
        valuesByMetric.computeIfAbsent(apm.getMetric(), k -> new ArrayList<>()).add(apm.getValue());
    }

    @Override
    public Map<String, Map<String, Number>> getResult() {
        Map<String, Map<String, Number>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> e : valuesByMetric.entrySet()) {
            List<Double> sorted = new ArrayList<>(e.getValue());
            Collections.sort(sorted);

            double average = sorted.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            Map<String, Number> stats = new LinkedHashMap<>();
            stats.put("minimum", formatNumber(sorted.get(0)));
            stats.put("median", formatNumber(calculateMedian(sorted)));
            stats.put("average", formatNumber(average));
            stats.put("max", formatNumber(sorted.get(sorted.size() - 1)));
            result.put(e.getKey(), stats);
        }
        return result;
    }

    @Override
    public String getOutputFileName() { return "apm.json"; }

    private double calculateMedian(List<Double> sorted) {
        int n = sorted.size();
        if (n % 2 == 1) return sorted.get(n / 2);
        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }

    private Number formatNumber(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return (long) value;
        }
        return value;
    }
}
