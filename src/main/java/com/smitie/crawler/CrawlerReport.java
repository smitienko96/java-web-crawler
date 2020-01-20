package com.smitie.crawler;

import lombok.Value;

import java.util.Set;

/**
 * Report for the crawler execution results.
 *
 * @author s.smitienko
 */
@Value
public class CrawlerReport {
    private Set<String> visitedUrls;
    private Long executionTime;
    private int serverErrorsNumber;
}
