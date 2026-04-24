package com.logparser.aggregator;

import com.logparser.entry.ApplicationLogEntry;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationLogAggregatorTest {
    @Test
    void getResult_emptyWhenNoEntries() {
        ApplicationLogAggregator aggregator = new ApplicationLogAggregator();
        assertTrue(aggregator.getResult().isEmpty());
    }

    @Test
    void getResult_countsLevelCorrectly() {
        ApplicationLogAggregator aggregator = new ApplicationLogAggregator();
        aggregator.add(new ApplicationLogEntry("t", "h", "ERROR", "err1"));
        aggregator.add(new ApplicationLogEntry("t", "h", "ERROR", "err2"));
        aggregator.add(new ApplicationLogEntry("t", "h", "INFO", "info1"));
        aggregator.add(new ApplicationLogEntry("t", "h", "DEBUG", "debug1"));
        aggregator.add(new ApplicationLogEntry("t", "h", "WARNING", "warn1"));

        Map<String, Integer> result = aggregator.getResult();
        assertEquals(2, result.get("ERROR"));
        assertEquals(1, result.get("INFO"));
        assertEquals(1, result.get("DEBUG"));
        assertEquals(1, result.get("WARNING"));
    }

    @Test
    void getResult_levelNormalizedToUpperCase() {
        ApplicationLogAggregator aggregator = new ApplicationLogAggregator();
        aggregator.add(new ApplicationLogEntry("t", "h", "error", "msg"));

        Map<String, Integer> result = aggregator.getResult();
        assertEquals(1, result.get("ERROR"));
        assertNull(result.get("error"));
    }

    @Test
    void getOutputFileName() {
        assertEquals("application.json", new ApplicationLogAggregator().getOutputFileName());
    }
}
