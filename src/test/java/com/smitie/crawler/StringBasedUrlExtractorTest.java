package com.smitie.crawler;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class StringBasedUrlExtractorTest {

    private static final String HTML = "<html><a href=\"https://www.opencodez.com\"><img src=\"https://www.opencodez.com/wp-content/uploads/2016/09/logo8.png\" alt=\"opencodez\"></a>" +
            "//<a href=\"https://www.opencodez.com\"><img src=\"https://www.opencodez.com/wp-content/uploads/2016/09/logo8.png\" alt=\"opencodez\"></a>" +
            "//<a href=\"https://www.opencodez.com\"><img src=\"https://www.opencodez.com/wp-content/uploads/2016/09/logo8.png\" alt=\"opencodez\"></a>" +
            "//<a href=\"https://www.opencodez.com\"><img src=\"https://www.opencodez.com/wp-content/uploads/2016/09/logo8.png\" alt=\"opencodez\"></a>" +
            "//<a href=\"https://www.opencodez.com\"><img src=\"https://www.opencodez.com/wp-content/uploads/2016/09/logo8.png\" alt=\"opencodez\"></a></html>";

    @Test
    public void given_html_with_links_when_extract_the_get_correct_number() {
        List<String> extract = StringBasedHtmlUrlExtractor.extract(HTML);
        assertEquals(5, extract.size());
    }
}
