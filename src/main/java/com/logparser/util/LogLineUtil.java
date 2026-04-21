package com.logparser.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogLineUtil {
    // matches key=value or key="value with spaces"
    private static final Pattern FIELD_PATTERN =
        Pattern.compile("(\\w+)=(?:\"([^\"]*)\"|([^\\s]+))");

    public static Map<String, String> parseFields(String line) {
        Map<String, String> fields = new LinkedHashMap<>();
        Matcher matcher = FIELD_PATTERN.matcher(line);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            fields.put(key, value);
        }
        return fields;
    }
}
