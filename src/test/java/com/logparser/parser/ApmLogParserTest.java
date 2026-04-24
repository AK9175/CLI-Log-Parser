package com.logparser.parser;

import com.logparser.entry.ApmLogEntry;
import com.logparser.util.LogLineUtil;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ApmLogParserTest {
    private final ApmLogParser parser = new ApmLogParser();

    @Test
    void canParse_returnsTrueForApmLine() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72"
        );
        assertTrue(parser.canParse(fields));
    }

    @Test
    void canParse_returnsFalseWhenMetricMissing() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:20Z level=INFO message=\"msg\" host=webserver1"
        );
        assertFalse(parser.canParse(fields));
    }

    @Test
    void canParse_returnsFalseWhenValueMissing() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1"
        );
        assertFalse(parser.canParse(fields));
    }

    @Test
    void parse_extractsMetricAndValue() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72"
        );
        ApmLogEntry entry = (ApmLogEntry) parser.parse(fields);
        assertEquals("cpu_usage_percent", entry.getMetric());
        assertEquals(72.0, entry.getValue());
        assertEquals("webserver1", entry.getHost());
        assertEquals("2024-02-24T16:22:15Z", entry.getTimestamp());
    }

    @Test
    void parse_handlesDecimalValue() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:15Z metric=load_avg host=webserver1 value=1.75"
        );
        ApmLogEntry entry = (ApmLogEntry) parser.parse(fields);
        assertEquals(1.75, entry.getValue());
    }
}
