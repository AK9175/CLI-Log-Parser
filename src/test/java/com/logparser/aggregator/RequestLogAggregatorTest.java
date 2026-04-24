package com.logparser.aggregator;

import com.logparser.entry.RequestLogEntry;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class RequestLogAggregatorTest {
    @Test
    void getResult_emptyWhenNoEntries() {
        RequestLogAggregator aggregator = new RequestLogAggregator();
        assertTrue(aggregator.getResult().isEmpty());
    }

    @Test
    void getResult_singleEntry() {
        RequestLogAggregator aggregator = new RequestLogAggregator();
        aggregator.add(new RequestLogEntry("t", "h", "POST", "/api/update", 202, 200));

        Map<String, Map<String, Object>> result = aggregator.getResult();
        Map<String, Object> route = result.get("/api/update");
        assertNotNull(route);

        @SuppressWarnings("unchecked")
        Map<String, Integer> rt = (Map<String, Integer>) route.get("response_times");
        assertEquals(200, rt.get("min"));
        assertEquals(200, rt.get("95_percentile"));
        assertEquals(200, rt.get("max"));

        @SuppressWarnings("unchecked")
        Map<String, Integer> sc = (Map<String, Integer>) route.get("status_codes");
        assertEquals(1, sc.get("2XX"));
        assertEquals(0, sc.get("4XX"));
        assertEquals(0, sc.get("5XX"));
    }

    @Test
    void getResult_groupsByRoute() {
        RequestLogAggregator aggregator = new RequestLogAggregator();
        aggregator.add(new RequestLogEntry("t", "h", "GET", "/api/status", 200, 100));
        aggregator.add(new RequestLogEntry("t", "h", "GET", "/api/status", 200, 150));
        aggregator.add(new RequestLogEntry("t", "h", "POST", "/api/status", 500, 300));
        aggregator.add(new RequestLogEntry("t", "h", "GET", "/api/status", 200, 180));

        @SuppressWarnings("unchecked")
        Map<String, Integer> rt = (Map<String, Integer>)
            aggregator.getResult().get("/api/status").get("response_times");

        assertEquals(100, rt.get("min"));
        assertEquals(300, rt.get("max"));
        assertEquals(300, rt.get("95_percentile")); // P95 of [100,150,180,300] → index 3
    }

    @Test
    void getResult_statusCodeCategorization() {
        RequestLogAggregator aggregator = new RequestLogAggregator();
        aggregator.add(new RequestLogEntry("t", "h", "GET", "/api/status", 200, 100));
        aggregator.add(new RequestLogEntry("t", "h", "GET", "/api/status", 404, 50));
        aggregator.add(new RequestLogEntry("t", "h", "POST", "/api/status", 500, 300));

        @SuppressWarnings("unchecked")
        Map<String, Integer> sc = (Map<String, Integer>)
            aggregator.getResult().get("/api/status").get("status_codes");

        assertEquals(1, sc.get("2XX"));
        assertEquals(1, sc.get("4XX"));
        assertEquals(1, sc.get("5XX"));
    }

    @Test
    void getOutputFileName() {
        assertEquals("request.json", new RequestLogAggregator().getOutputFileName());
    }
}
