package com.smitie.crawler;

import java.io.IOException;

public interface Crawler {

    CrawlerReport run() throws IOException, InterruptedException;
}
