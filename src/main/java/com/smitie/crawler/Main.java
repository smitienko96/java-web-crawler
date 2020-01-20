package com.smitie.crawler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.load(Main.class.getClassLoader().getResourceAsStream("crawler.properties"));
        CrawlerConfig config = new CrawlerConfig(properties);
        Crawler crawler = new SimpleWebCrawler(config);
        CrawlerReport report = crawler.run();
        int serverErrorsNumber = report.getServerErrorsNumber();
        int visitedUrlsNumber = report.getVisitedUrls().size();
        Long executionTime = report.getExecutionTime();
        log.info("Crawler has finished: visited {} urls in {} milliseconds with {} internal server error(s)", visitedUrlsNumber, executionTime, serverErrorsNumber);
    }


}
