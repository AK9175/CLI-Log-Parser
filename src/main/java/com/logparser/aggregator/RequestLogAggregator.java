package com.logparser.aggregator;

import com.logparser.entry.LogEntry;
import com.logparser.entry.RequestLogEntry;
import java.util.*;

public class RequestLogAggregator implements LogAggregator {
    private final Map<String, List<RequestLogEntry>> entriesByRoute = new LinkedHashMap<>();

    @Override
    public void add(LogEntry entry) {
        RequestLogEntry req = (RequestLogEntry) entry;
        entriesByRoute.computeIfAbsent(req.getUrl(), k -> new ArrayList<>()).add(req);
    }

    @Override
    public Map<String, Map<String, Object>> getResult() {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<RequestLogEntry>> e : entriesByRoute.entrySet()) {
            List<Integer> times = new ArrayList<>();
            int count2xx = 0, count4xx = 0, count5xx = 0;

            for (RequestLogEntry req : e.getValue()) {
                times.add(req.getResponseTimeMs());
                int status = req.getStatusCode();
                if (status >= 200 && status < 300) count2xx++;
                else if (status >= 400 && status < 500) count4xx++;
                else if (status >= 500 && status < 600) count5xx++;
            }

            Collections.sort(times);

            Map<String, Integer> responseTimes = new LinkedHashMap<>();
            responseTimes.put("min", times.get(0));
            responseTimes.put("95_percentile", calculateP95(times));
            responseTimes.put("max", times.get(times.size() - 1));

            Map<String, Integer> statusCodes = new LinkedHashMap<>();
            statusCodes.put("2XX", count2xx);
            statusCodes.put("4XX", count4xx);
            statusCodes.put("5XX", count5xx);

            Map<String, Object> routeStats = new LinkedHashMap<>();
            routeStats.put("response_times", responseTimes);
            routeStats.put("status_codes", statusCodes);

            result.put(e.getKey(), routeStats);
        }
        return result;
    }

    @Override
    public String getOutputFileName() { return "request.json"; }

    private int calculateP95(List<Integer> sorted) {
        int index = (int) Math.ceil(0.95 * sorted.size()) - 1;
        return sorted.get(Math.max(0, index));
    }
}
