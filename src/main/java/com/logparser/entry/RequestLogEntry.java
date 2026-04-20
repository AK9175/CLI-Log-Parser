package com.logparser.entry;

public class RequestLogEntry extends LogEntry {
    private final String method;
    private final String url;
    private final int statusCode;
    private final int responseTimeMs;

    public RequestLogEntry(String timestamp, String host, String method, String url, int statusCode, int responseTimeMs) {
        super(timestamp, host);
        this.method = method;
        this.url = url;
        this.statusCode = statusCode;
        this.responseTimeMs = responseTimeMs;
    }

    public String getMethod() { return method; }
    public String getUrl() { return url; }
    public int getStatusCode() { return statusCode; }
    public int getResponseTimeMs() { return responseTimeMs; }
}
