package com.smitie.crawler;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple String-based html url extractor.
 *
 * @author s.smitienko
 */
public class StringBasedHtmlUrlExtractor {

    private static final String HREF = "href=\"";
    private static final String LINK_CLOSING_QUOTE = "\"";

    /**
     * Seeks for all urls which a given page contains.
     *
     * @param html input html page
     * @return list of urls
     */
    public static List<String> extract(final String html) {
        List<String> result = new ArrayList<>(70);
        int pos, start = 0;
        while ((pos = html.indexOf(HREF, start)) != -1) {
            int from = pos + HREF.length();
            int to = html.indexOf(LINK_CLOSING_QUOTE, from);
            result.add(html.substring(from, to));
            start = to;
        }
        return result;
    }


}


