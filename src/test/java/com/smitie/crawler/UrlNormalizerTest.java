package com.smitie.crawler;

import org.junit.Test;

import java.util.Optional;

import static com.smitie.crawler.UrlNormalizer.normalize;
import static org.junit.Assert.*;

public class UrlNormalizerTest {

    private static final String RELATIVE = "/requests";
    private static final String BASE = "https://smitie.com";
    private static final String BLANK = "   ";
    private static final String SHARP = "#";

    @Test
    public void given_relative_url_when_normalize_then_get_absolute() {
        Optional<String> normalized = normalize(RELATIVE, BASE);
        assertTrue(normalized.isPresent());
        assertEquals(BASE + RELATIVE, normalized.get());
    }

    @Test
    public void given_based_url_when_normalize_then_return_base() {
        Optional<String> normalized = normalize(BASE, BASE);
        assertTrue(normalized.isPresent());
        assertEquals(BASE , normalized.get());
    }

    @Test
    public void given_blank_url_when_normalize_then_return_empty() {
        Optional<String> normalized = normalize(BLANK, BASE);
        assertFalse(normalized.isPresent());
    }

    @Test
    public void given_sharp_when_normalize_then_return_empty() {
        Optional<String> normalized = normalize(SHARP, BASE);
        assertFalse(normalized.isPresent());
    }
}
