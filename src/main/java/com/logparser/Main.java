package com.logparser;

import com.logparser.aggregator.ApmLogAggregator;
import com.logparser.aggregator.ApplicationLogAggregator;
import com.logparser.aggregator.LogAggregator;
import com.logparser.aggregator.RequestLogAggregator;
import com.logparser.parser.ApmLogParser;
import com.logparser.parser.ApplicationLogParser;
import com.logparser.parser.LogParser;
import com.logparser.parser.RequestLogParser;
import com.logparser.util.JsonOutputWriter;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String inputFile = parseInputFile(args);
        if (inputFile == null) {
            System.err.println("Usage: java -jar log-parser.jar --file <filename.txt>");
            System.exit(1);
        }

        List<LogParser> parsers = List.of(
            new ApmLogParser(),
            new ApplicationLogParser(),
            new RequestLogParser()
        );
        List<LogAggregator> aggregators = List.of(
            new ApmLogAggregator(),
            new ApplicationLogAggregator(),
            new RequestLogAggregator()
        );

        LogFileProcessor processor = new LogFileProcessor(parsers, aggregators);
        processor.process(inputFile);

        for (LogAggregator aggregator : aggregators) {
            JsonOutputWriter.write(aggregator.getOutputFileName(), aggregator.getResult());
            System.out.println("Written: " + aggregator.getOutputFileName());
        }
    }

    private static String parseInputFile(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("--file".equals(args[i])) {
                return args[i + 1];
            }
        }
        return null;
    }
}
