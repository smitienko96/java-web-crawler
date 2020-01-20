package com.smitie.crawler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.asynchttpclient.uri.Uri;

import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.currentTimeMillis;
import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.config;

/**
 * Simple html web crawler.
 *
 * @author s.smitienko
 */
@Slf4j
public class SimpleWebCrawler implements Crawler {

    private final AsyncHttpClient asyncClient;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Queue<String> urlsToProcess = new ConcurrentLinkedQueue<>();
    private final AtomicInteger internalServerErrorsNumber = new AtomicInteger(0);
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private final int allowedServerErrorsPercentage;
    private final AtomicInteger runningClientThreads = new AtomicInteger(0);
    private final int maxRunningClientThreads;

    public SimpleWebCrawler(final CrawlerConfig crawlerConfig) {
        int poolSize = crawlerConfig.threadPoolSize == 0 ? Runtime.getRuntime().availableProcessors() : crawlerConfig.threadPoolSize;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        this.asyncClient = configureClient(crawlerConfig);
        this.allowedServerErrorsPercentage = crawlerConfig.allowedServerErrorsPercentage;
        this.urlsToProcess.add(crawlerConfig.rootUrl);
        this.maxRunningClientThreads = crawlerConfig.maxRunningClientThreads;
    }

    /**
     * Runs crawler starting with some root url.
     *
     * @return report
     */
    @Override
    @SneakyThrows
    public CrawlerReport run() {
        long startTime = currentTimeMillis();
        while (!shouldStopCrawling()) {
            String baseUrl = urlsToProcess.poll();
            if (baseUrl == null || visitedUrls.contains(baseUrl)) {
                continue;
            }
            if (runningClientThreads.get() >= maxRunningClientThreads) {
                continue;
            }
            visitedUrls.add(baseUrl);
            runningClientThreads.incrementAndGet();
            processUrl(baseUrl);
        }
        long duration = currentTimeMillis() - startTime;
        return new CrawlerReport(new HashSet<>(visitedUrls), duration, internalServerErrorsNumber.get());
    }


    /**
     * Processes given url, picking up all of the urls this page contains and pushing them into queue.
     *
     * @param url root url
     */
    private void processUrl(final String url) {
        log.info("sending request to {}", url);
        asyncClient
                .prepareGet(String.valueOf(Uri.create(url)))
                .execute()
                .toCompletableFuture()
                .thenAcceptAsync(resp -> {
                    runningClientThreads.decrementAndGet();
                    if (resp == null) {
                        return;
                    }
                    if (isInternalServerError(resp)) {
                        internalServerErrorsNumber.getAndIncrement();
                        return;
                    }
                    StringBasedHtmlUrlExtractor
                            .extract(resp.getResponseBody())
                            .stream()
                            .map(s -> UrlNormalizer.normalize(s, url))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .filter(s -> !visitedUrls.contains(s))
                            .forEach(urlsToProcess::add);
                }, threadPoolExecutor)
                .whenComplete((__, th) -> log.info("Crawler current state: \nurls to process queue size: {} \n" +
                                "visited urls: {} \n" +
                                "client active connections: {}"
                        , urlsToProcess.size(), visitedUrls.size(), asyncClient.getClientStats().getTotalActiveConnectionCount()))
                .exceptionally(ex -> {
                    log.error("Exception while processing url " + url, ex);
                    return null;
                });
    }

    /**
     * Checks if response from the server
     *
     * @param resp response
     * @return result
     */
    private boolean isInternalServerError(final Response resp) {
        return resp.getStatusCode() >= 500 && resp.getStatusCode() <= 599;
    }

    /**
     * Creates async http client with given configuration object.
     *
     * @param crawlerConfig config object
     * @return client
     */
    private AsyncHttpClient configureClient(final CrawlerConfig crawlerConfig) {
        return asyncHttpClient(
                config()
                        .setIoThreadsCount(300)
                        .setFollowRedirect(true)
                        .setConnectTimeout(crawlerConfig.httpClientConnectTimeout)
                        .setRequestTimeout(crawlerConfig.httpClientRequestTimeout)
                        .setReadTimeout(crawlerConfig.httpClientReadTimeout)
                        .setHandshakeTimeout(crawlerConfig.httpClientHandshakeTimeout)
        );

    }

    /**
     * Checks if the number of server errors exceeded some defined threshold.
     *
     * @return result
     */
    private boolean serverErrorPercentageExceeded() {
        if (visitedUrls.size() == 0) {
            return false;
        }
        return (internalServerErrorsNumber.get() / visitedUrls.size()) * 100 >= allowedServerErrorsPercentage;
    }

    /**
     * Checks the conditions which mark that crawling should be finished.
     *
     * @return result
     */
    private boolean shouldStopCrawling() {
        if (serverErrorPercentageExceeded()) {
            return true;
        }
        return runningClientThreads.get() == 0 && urlsToProcess.isEmpty()
                && threadPoolExecutor.getActiveCount() == 0 && threadPoolExecutor.getQueue().isEmpty();
    }

}