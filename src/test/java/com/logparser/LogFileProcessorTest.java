package com.logparser;

import com.logparser.aggregator.*;
import com.logparser.parser.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class LogFileProcessorTest {
    @TempDir
    Path tempDir;

    private LogFileProcessor buildProcessor(List<LogAggregator> aggregators) {
        return new LogFileProcessor(
            List.of(new ApmLogParser(), new ApplicationLogParser(), new RequestLogParser()),
            aggregators
        );
    }

    @Test
    void process_parsesAllThreeLogTypes() throws Exception {
        File input = tempDir.resolve("test.txt").toFile();
        try (PrintWriter pw = new PrintWriter(input)) {
            pw.println("timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72");
            pw.println("timestamp=2024-02-24T16:22:20Z level=INFO message=\"Maintenance starting\" host=webserver1");
            pw.println("timestamp=2024-02-24T16:22:25Z request_method=POST request_url=\"/api/update\" response_status=202 response_time_ms=200 host=webserver1");
        }

        List<LogAggregator> aggregators = List.of(
            new ApmLogAggregator(), new ApplicationLogAggregator(), new RequestLogAggregator()
        );
        buildProcessor(aggregators).process(input.getAbsolutePath());

        assertFalse(((Map<?, ?>) aggregators.get(0).getResult()).isEmpty(), "APM result should not be empty");
        assertFalse(((Map<?, ?>) aggregators.get(1).getResult()).isEmpty(), "App result should not be empty");
        assertFalse(((Map<?, ?>) aggregators.get(2).getResult()).isEmpty(), "Request result should not be empty");
    }

    @Test
    void process_corruptedLinesAreSkipped() throws Exception {
        File input = tempDir.resolve("test.txt").toFile();
        try (PrintWriter pw = new PrintWriter(input)) {
            pw.println("this is a completely corrupted line");
            pw.println("timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72");
            pw.println("another bad line!!!!");
        }

        List<LogAggregator> aggregators = List.of(
            new ApmLogAggregator(), new ApplicationLogAggregator(), new RequestLogAggregator()
        );
        buildProcessor(aggregators).process(input.getAbsolutePath());

        Map<?, ?> apmResult = (Map<?, ?>) aggregators.get(0).getResult();
        assertEquals(1, apmResult.size(), "Only the valid APM line should be counted");
    }

    @Test
    void process_emptyFileProducesEmptyResults() throws Exception {
        File input = tempDir.resolve("empty.txt").toFile();
        input.createNewFile();

        List<LogAggregator> aggregators = List.of(
            new ApmLogAggregator(), new ApplicationLogAggregator(), new RequestLogAggregator()
        );
        buildProcessor(aggregators).process(input.getAbsolutePath());

        for (LogAggregator agg : aggregators) {
            assertTrue(((Map<?, ?>) agg.getResult()).isEmpty());
        }
    }
}
