package com.smitie.crawler;


import java.util.Properties;

/**
 * Crawler configuration class.
 *
 * @author s.smitienko
 */
public class CrawlerConfig {

    public final String rootUrl;
    public final int httpClientConnectTimeout;
    public final int httpClientReadTimeout;
    public final int httpClientRequestTimeout;
    public final int threadPoolSize;
    public final int allowedServerErrorsPercentage;
    public final int maxRunningClientThreads;
    public final int httpClientHandshakeTimeout;


    public CrawlerConfig(final Properties properties) {
        rootUrl = properties.getProperty("rootUrl");
        threadPoolSize = Integer.parseInt(properties.getProperty("threadPoolSize"));
        httpClientConnectTimeout = Integer.parseInt(properties.getProperty("httpClientConnectTimeout"));
        httpClientReadTimeout = Integer.parseInt(properties.getProperty("httpClientReadTimeout"));
        httpClientRequestTimeout = Integer.parseInt(properties.getProperty("httpClientRequestTimeout"));
        allowedServerErrorsPercentage = Integer.parseInt(properties.getProperty("allowedServerErrorsPercentage"));
        maxRunningClientThreads = Integer.parseInt(properties.getProperty("maxRunningClientThreads"));
        httpClientHandshakeTimeout = Integer.parseInt(properties.getProperty("httpClientHandshakeTimeout"));
    }
}
