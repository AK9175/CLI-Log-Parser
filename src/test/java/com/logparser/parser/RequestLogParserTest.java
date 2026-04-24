package com.logparser.parser;

import com.logparser.entry.RequestLogEntry;
import com.logparser.util.LogLineUtil;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class RequestLogParserTest {
    private final RequestLogParser parser = new RequestLogParser();

    @Test
    void canParse_returnsTrueForRequestLine() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:25Z request_method=POST request_url=\"/api/update\" response_status=202 response_time_ms=200 host=webserver1"
        );
        assertTrue(parser.canParse(fields));
    }

    @Test
    void canParse_returnsFalseWhenFieldsMissing() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:15Z metric=cpu_usage_percent host=webserver1 value=72"
        );
        assertFalse(parser.canParse(fields));
    }

    @Test
    void parse_extractsAllFields() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=2024-02-24T16:22:25Z request_method=POST request_url=\"/api/update\" response_status=202 response_time_ms=200 host=webserver1"
        );
        RequestLogEntry entry = (RequestLogEntry) parser.parse(fields);
        assertEquals("POST", entry.getMethod());
        assertEquals("/api/update", entry.getUrl());
        assertEquals(202, entry.getStatusCode());
        assertEquals(200, entry.getResponseTimeMs());
        assertEquals("webserver1", entry.getHost());
    }

    @Test
    void parse_handlesGetRequest() {
        Map<String, String> fields = LogLineUtil.parseFields(
            "timestamp=t request_method=GET request_url=\"/api/status\" response_status=200 response_time_ms=100 host=h"
        );
        RequestLogEntry entry = (RequestLogEntry) parser.parse(fields);
        assertEquals("GET", entry.getMethod());
        assertEquals("/api/status", entry.getUrl());
        assertEquals(200, entry.getStatusCode());
    }
}
