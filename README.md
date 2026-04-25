[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/GdbHO-KB)

# CLI Log Parser

A command-line Java application that parses structured log files, classifies entries by type, and writes aggregated statistics to JSON output files.

---

## Features

- Parses a `.txt` log file where each line is a log entry in `key=value` format
- Classifies each line into one of three log types:
  - **APM Logs** — application performance metrics (CPU, memory, disk, network, etc.)
  - **Application Logs** — severity-level events (INFO, ERROR, DEBUG, WARNING)
  - **Request Logs** — HTTP request details (method, route, status code, response time)
- Computes aggregations per log type and writes results to separate JSON files
- Silently skips corrupted or unrecognized lines
- Extensible design — new log types can be added without modifying existing code

---

## Design Patterns

### Strategy Pattern
`LogParser` and `LogAggregator` are defined as interfaces. Each log type has its own concrete implementation of both. This allows parsing and aggregation behavior to be swapped or extended independently.

### Factory / Registry Pattern
`LogFileProcessor` holds a ordered list of parsers and a corresponding list of aggregators. For each line, it iterates through the parsers until one matches (`canParse`), then delegates to the corresponding aggregator. Adding a new log type only requires registering a new parser-aggregator pair in `Main` — zero changes to existing classes (Open/Closed Principle).

---

## Project Structure

```
.
├── input.txt                            # Sample log input file
├── apm.json                             # Sample APM output
├── application.json                     # Sample Application output
├── request.json                         # Sample Request output
├── pom.xml
└── src/
    ├── main/java/com/logparser/
    │   ├── Main.java                    # Entry point
    │   ├── LogFileProcessor.java        # Reads file, routes lines to parsers/aggregators
    │   ├── entry/
    │   │   ├── LogEntry.java            # Abstract base class
    │   │   ├── ApmLogEntry.java
    │   │   ├── ApplicationLogEntry.java
    │   │   └── RequestLogEntry.java
    │   ├── parser/
    │   │   ├── LogParser.java           # Interface
    │   │   ├── ApmLogParser.java
    │   │   ├── ApplicationLogParser.java
    │   │   └── RequestLogParser.java
    │   ├── aggregator/
    │   │   ├── LogAggregator.java       # Interface
    │   │   ├── ApmLogAggregator.java
    │   │   ├── ApplicationLogAggregator.java
    │   │   └── RequestLogAggregator.java
    │   └── util/
    │       ├── LogLineUtil.java         # Parses key=value pairs (handles quoted values)
    │       └── JsonOutputWriter.java    # Pretty-prints output via Jackson
    └── test/java/com/logparser/
        ├── LogFileProcessorTest.java
        ├── parser/
        │   ├── ApmLogParserTest.java
        │   ├── ApplicationLogParserTest.java
        │   └── RequestLogParserTest.java
        └── aggregator/
            ├── ApmLogAggregatorTest.java
            ├── ApplicationLogAggregatorTest.java
            └── RequestLogAggregatorTest.java
```

---

## How to Run

### Prerequisites
- Java 11+
- Maven 3.6+

### Build

```bash
mvn package
```

This produces `target/log-parser.jar` — a fat jar with all dependencies bundled.

### Run

```bash
java -jar target/log-parser.jar --file input.txt
```

### Output

Three JSON files are created in the current directory:

| File | Contents |
|---|---|
| `apm.json` | min, median, average, max per metric |
| `application.json` | count per severity level |
| `request.json` | response time stats (min, P95, max) and status code counts per route |

#### apm.json
```json
{
  "cpu_usage_percent": {
    "minimum": 65,
    "median": 68.5,
    "average": 68.5,
    "max": 72
  }
}
```

#### application.json
```json
{
  "INFO": 3,
  "ERROR": 2,
  "DEBUG": 1,
  "WARNING": 1
}
```

#### request.json
```json
{
  "/api/status": {
    "response_times": {
      "min": 100,
      "95_percentile": 300,
      "max": 300
    },
    "status_codes": {
      "2XX": 3,
      "4XX": 0,
      "5XX": 1
    }
  }
}
```

---

## Run Tests

```bash
mvn test
```

31 unit and integration tests covering parsers, aggregators, and the full processing pipeline.

```
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
