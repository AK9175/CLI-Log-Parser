package com.logparser;

import com.logparser.aggregator.LogAggregator;
import com.logparser.entry.LogEntry;
import com.logparser.parser.LogParser;
import com.logparser.util.LogLineUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LogFileProcessor {
    private final List<LogParser> parsers;
    private final List<LogAggregator> aggregators;

    public LogFileProcessor(List<LogParser> parsers, List<LogAggregator> aggregators) {
        this.parsers = parsers;
        this.aggregators = aggregators;
    }

    public void process(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                processLine(line);
            }
        }
    }

    private void processLine(String line) {
        Map<String, String> fields = LogLineUtil.parseFields(line);
        for (int i = 0; i < parsers.size(); i++) {
            if (parsers.get(i).canParse(fields)) {
                try {
                    LogEntry entry = parsers.get(i).parse(fields);
                    aggregators.get(i).add(entry);
                } catch (Exception ignored) {
                    // corrupted line — skip silently
                }
                return;
            }
        }
    }

    public List<LogAggregator> getAggregators() {
        return Collections.unmodifiableList(aggregators);
    }
}
