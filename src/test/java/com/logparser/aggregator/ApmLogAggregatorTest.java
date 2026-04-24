package com.logparser.aggregator;

import com.logparser.entry.ApmLogEntry;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ApmLogAggregatorTest {
    @Test
    void getResult_emptyWhenNoEntries() {
        ApmLogAggregator aggregator = new ApmLogAggregator();
        assertTrue(aggregator.getResult().isEmpty());
    }

    @Test
    void getResult_singleEntry() {
        ApmLogAggregator aggregator = new ApmLogAggregator();
        aggregator.add(new ApmLogEntry("t", "h", "cpu_usage_percent", 72));

        Map<String, Map<String, Number>> result = aggregator.getResult();
        Map<String, Number> cpu = result.get("cpu_usage_percent");

        assertEquals(72L, cpu.get("minimum"));
        assertEquals(72L, cpu.get("median"));
        assertEquals(72L, cpu.get("average"));
        assertEquals(72L, cpu.get("max"));
    }

    @Test
    void getResult_multipleEntriesSameMetric() {
        ApmLogAggregator aggregator = new ApmLogAggregator();
        aggregator.add(new ApmLogEntry("t", "h", "cpu_usage_percent", 60));
        aggregator.add(new ApmLogEntry("t", "h", "cpu_usage_percent", 78));
        aggregator.add(new ApmLogEntry("t", "h", "cpu_usage_percent", 90));

        Map<String, Number> cpu = aggregator.getResult().get("cpu_usage_percent");
        assertEquals(60L, cpu.get("minimum"));
        assertEquals(78L, cpu.get("median"));
        assertEquals(76L, cpu.get("average"));
        assertEquals(90L, cpu.get("max"));
    }

    @Test
    void getResult_evenCountMedianIsAverage() {
        ApmLogAggregator aggregator = new ApmLogAggregator();
        aggregator.add(new ApmLogEntry("t", "h", "memory_usage_percent", 5));
        aggregator.add(new ApmLogEntry("t", "h", "memory_usage_percent", 78));

        Map<String, Number> mem = aggregator.getResult().get("memory_usage_percent");
        assertEquals(41.5, mem.get("median"));
        assertEquals(41.5, mem.get("average"));
    }

    @Test
    void getResult_multipleMetrics() {
        ApmLogAggregator aggregator = new ApmLogAggregator();
        aggregator.add(new ApmLogEntry("t", "h", "cpu_usage_percent", 72));
        aggregator.add(new ApmLogEntry("t", "h", "memory_usage_percent", 85));

        Map<String, Map<String, Number>> result = aggregator.getResult();
        assertTrue(result.containsKey("cpu_usage_percent"));
        assertTrue(result.containsKey("memory_usage_percent"));
    }

    @Test
    void getOutputFileName() {
        assertEquals("apm.json", new ApmLogAggregator().getOutputFileName());
    }
}
