package com.github.emotion.httpproxy.utils;

/**
 * @author emotion
 * @date 19/11/2016
 */
public class HttpHeaderUtils {
    /**
     * These are the "hop-by-hop" headers that should not be copied.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html
     * I use an HttpClient HeaderGroup class instead of Set&lt;String&gt; because this
     * approach does case insensitive lookup faster.
     */
    private static final String[] hopByHopHeaders;

    static {
        hopByHopHeaders = new String[]{
                "Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization",
                "TE", "Trailers", "Transfer-Encoding", "Upgrade"};
    }

    public static boolean isHopByHopHeader(String headerName) {
        for (String header : hopByHopHeaders) {
            if (header.equalsIgnoreCase(headerName)) {
                return true;
            }
        }
        return false;
    }
}
