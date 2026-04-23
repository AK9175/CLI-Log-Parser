package com.logparser.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class JsonOutputWriter {
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    public static void write(String fileName, Object data) throws IOException {
        MAPPER.writeValue(new File(fileName), data);
    }
}
