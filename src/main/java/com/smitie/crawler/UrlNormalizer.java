package com.smitie.crawler;

import org.asynchttpclient.uri.Uri;

import java.util.Optional;

/**
 * Url normalizer.
 *
 * @author s.smitienko
 */
public class UrlNormalizer {

    /**
     * Normalizes given url if it's needed.
     *
     * @param url     url
     * @param baseUrl base url in case of relative url path
     * @return optional of normalized url
     */
    public static Optional<String> normalize(final String url,  String baseUrl) {
        String strippedUrl = url.strip();
        baseUrl = cutRoot(baseUrl);
        if (isBlank(strippedUrl) || strippedUrl.startsWith("#")) {
            return Optional.empty();
        }

        if (strippedUrl.startsWith("/")) {
            return Optional.of(baseUrl + strippedUrl);
        }

        if (strippedUrl.startsWith(baseUrl)) {
            return Optional.of(strippedUrl);
        }
        return Optional.empty();
    }

    /**
     * Checks if a given sequence of characters id blank.
     *
     * @param cs input char sequence
     * @return result
     */
    private static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Cuts root url.
     *
     * @param input base
     * @return cut url
     */
    private static String cutRoot(final String input) {
        Uri url = Uri.create(input);
        String port = (url.getPort() == -1 ? "" : ":" + url.getPort());
        return url.getScheme() + "://" + url.getHost() + port;
    }
}
