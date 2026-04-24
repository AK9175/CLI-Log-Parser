package com.logparser.parser;

import com.logparser.entry.ApplicationLogEntry;
import com.logparser.util.LogLineUtil;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationLogParserTest {
    private final ApplicationLogParser parser = new ApplicationLogParser();

    @Test
    void canParse_returnsTrueForApplicationLine() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:20Z level=INFO message=\"Scheduled maintenance\" host=webserver1"
        );
        assertTrue(parser.canParse(fields));
    }

    @Test
    void canParse_returnsFalseForApmLine() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72"
        );
        assertFalse(parser.canParse(fields));
    }

    @Test
    void parse_extractsLevelAndMessage() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:35Z level=ERROR message=\"Update process failed\" host=webserver1"
        );
        ApplicationLogEntry entry = (ApplicationLogEntry) parser.parse(fields);
        assertEquals("ERROR", entry.getLevel());
        assertEquals("Update process failed", entry.getMessage());
        assertEquals("webserver1", entry.getHost());
    }

    @Test
    void parse_handlesAllSeverityLevels() {
        for (String level : new String[]{"INFO", "ERROR", "DEBUG", "WARNING"}) {
            Map<String, String> fields = LogLineUtil.parseFields(
                "timestamp=t level=" + level + " message=\"msg\" host=h"
            );
            ApplicationLogEntry entry = (ApplicationLogEntry) parser.parse(fields);
            assertEquals(level, entry.getLevel());
        }
    }
}
