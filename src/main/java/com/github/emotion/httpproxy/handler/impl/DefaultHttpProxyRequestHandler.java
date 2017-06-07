package com.github.emotion.httpproxy.handler.impl;

import com.github.emotion.httpproxy.handler.HttpProxyRequestHandler;
import com.github.emotion.httpproxy.HttpProxyURI;
import com.github.emotion.httpproxy.utils.HttpHeaderUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * @author emotion
 * @date 19/11/2016
 */
public class DefaultHttpProxyRequestHandler implements HttpProxyRequestHandler {
    private static final BitSet asciiQueryChars;

    static {
        char[] c_unreserved = "_-!.~'()*".toCharArray();//plus alphanum
        char[] c_punct = ",;:$&+=".toCharArray();
        char[] c_reserved = "?/[]@".toCharArray();//plus punct

        asciiQueryChars = new BitSet(128);
        for (char c = 'a'; c <= 'z'; c++) asciiQueryChars.set((int) c);
        for (char c = 'A'; c <= 'Z'; c++) asciiQueryChars.set((int) c);
        for (char c = '0'; c <= '9'; c++) asciiQueryChars.set((int) c);
        for (char c : c_unreserved) asciiQueryChars.set((int) c);
        for (char c : c_punct) asciiQueryChars.set((int) c);
        for (char c : c_reserved) asciiQueryChars.set((int) c);

        asciiQueryChars.set((int) '%');//leave existing percent escapes in place
    }

    private boolean doForwardIP = false;

    /**
     * Encodes characters in the query or fragment part of the URI.
     * <p>
     * <p>Unfortunately, an incoming URI sometimes has characters disallowed by the spec.  HttpClient
     * insists that the outgoing proxied request has a valid URI because it uses Java's {@link URI}.
     * To be more forgiving, we must escape the problematic characters.  See the URI class for the
     * spec.
     *
     * @param in example: name=value&amp;foo=bar#fragment
     */
    private static CharSequence encodeUriQuery(CharSequence in) {
        //Note that I can't simply use URI.java to encode because it will escape pre-existing escaped things.
        StringBuilder outBuf = null;
        Formatter formatter = null;
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            boolean escape = true;
            if (c < 128) {
                if (asciiQueryChars.get((int) c)) {
                    escape = false;
                }
            } else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {//not-ascii
                escape = false;
            }
            if (!escape) {
                if (outBuf != null)
                    outBuf.append(c);
            } else {
                //escape
                if (outBuf == null) {
                    outBuf = new StringBuilder(in.length() + 5 * 3);
                    outBuf.append(in, 0, i);
                    formatter = new Formatter(outBuf);
                }
                //leading %, 0 padded, width 2, capital hex
                formatter.format("%%%02X", (int) c);//TODO
            }
        }
        return outBuf != null ? outBuf : in;
    }

    public boolean isDoForwardIP() {
        return doForwardIP;
    }

    public void setDoForwardIP(boolean doForwardIP) {
        this.doForwardIP = doForwardIP;
    }

    public HttpUriRequest interpret(HttpServletRequest httpServletRequest, HttpProxyURI targetURI) throws IOException {
        // Make the Request
        //note: we won't transfer the protocol version because I'm not sure it would truly be compatible
        String method = httpServletRequest.getMethod();
        String proxyRequestUri = rewriteUrlFromRequest(httpServletRequest, targetURI);
        RequestBuilder requestBuilder = RequestBuilder.create(method)
                .setUri(proxyRequestUri);
        //spec: RFC 2616, sec 4.3: either of these two headers signal that there is a message body.
        if (httpServletRequest.getHeader(HttpHeaders.CONTENT_LENGTH) != null ||
                httpServletRequest.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
            HttpEntity httpEntity = newProxyHttpEntity(method, proxyRequestUri, httpServletRequest);
            if (httpEntity != null) {
                requestBuilder.setEntity(httpEntity);
            }
        }
        List<Header> headerList = copyRequestHeaders(httpServletRequest, targetURI);
        for (Header header : headerList) {
            requestBuilder.addHeader(header);
        }
        if (this.doForwardIP) {
            Header header = setXForwardedForHeader(httpServletRequest);
            if (header != null) {
                requestBuilder.addHeader(header);
            }
        }
        return requestBuilder.build();
    }

    /**
     * Reads the request URI from {@code servletRequest} and rewrites it, considering targetUri.
     * It's used to make the new request.
     */
    private String rewriteUrlFromRequest(HttpServletRequest servletRequest, HttpProxyURI targetURI) {
        StringBuilder uri = new StringBuilder(500);
        uri.append(getTargetUri(servletRequest, targetURI));
        // Handle the path given to the servlet
        if (servletRequest.getPathInfo() != null) {//ex: /my/path.html
            uri.append(encodeUriQuery(servletRequest.getPathInfo()));
        }
        // Handle the query string & fragment
        String queryString = servletRequest.getQueryString();//ex:(following '?'): name=value&foo=bar#fragment
        //split off fragment from queryString, updating queryString if found
        if (queryString != null) {
            int fragIdx = queryString.indexOf('#');
            if (fragIdx >= 0) {
                queryString = queryString.substring(0, fragIdx);
            }
        }

        queryString = rewriteQueryStringFromRequest(servletRequest, queryString);
        if (queryString != null && queryString.length() > 0) {
            uri.append('?');
            uri.append(encodeUriQuery(queryString));
        }

        return uri.toString();
    }

    private HttpEntity newProxyHttpEntity(String method, String proxyRequestUri, HttpServletRequest servletRequest) throws IOException {
        // Add the input entity (streamed)
        //  note: we don't bother ensuring we close the servletInputStream since the container handles it
        return new InputStreamEntity(servletRequest.getInputStream(), getContentLength(servletRequest));
    }

    /**
     * Copy request headers from the servlet client to the proxy request.
     */
    private List<Header> copyRequestHeaders(HttpServletRequest servletRequest, HttpProxyURI targetURI) {
        // Get an Enumeration of all of the header names sent by the client
        Enumeration<String> enumerationOfHeaderNames = servletRequest.getHeaderNames();
        List<Header> headerList = new ArrayList<Header>();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            String headerName = enumerationOfHeaderNames.nextElement();
            Header header = copyRequestHeader(servletRequest, headerName, targetURI);
            if (header != null) {
                headerList.add(header);
            }
        }
        return headerList;
    }

    /**
     * Copy a request header from the servlet client to the proxy request.
     * This is easily overwritten to filter out certain headers if desired.
     */
    private BasicHeader copyRequestHeader(HttpServletRequest servletRequest, String headerName, HttpProxyURI targetURI) {
        //Instead the content-length is effectively set via InputStreamEntity
        if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH))
            return null;
        if (HttpHeaderUtils.isHopByHopHeader(headerName))
            return null;

        @SuppressWarnings("unchecked")
        Enumeration<String> headers = servletRequest.getHeaders(headerName);
        while (headers.hasMoreElements()) {//sometimes more than one value
            String headerValue = headers.nextElement();
            // In case the proxy host is running multiple virtual servers,
            // rewrite the Host header to ensure that we get content from
            // the correct virtual server
            if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                HttpHost host = getTargetHost(servletRequest, targetURI);
                headerValue = host.getHostName();
                if (host.getPort() != -1)
                    headerValue += ":" + host.getPort();
            } else if (headerName.equalsIgnoreCase(org.apache.http.cookie.SM.COOKIE)) {
                headerValue = getRealCookie(headerValue);
            }
            return new BasicHeader(headerName, headerValue);
        }
        return null;
    }

    private Header setXForwardedForHeader(HttpServletRequest servletRequest) {
        if (doForwardIP) {
            String headerName = "X-Forwarded-For";
            String newHeader = servletRequest.getRemoteAddr();
            String existingHeader = servletRequest.getHeader(headerName);
            if (existingHeader != null) {
                newHeader = existingHeader + ", " + newHeader;
            }
            return new BasicHeader(headerName, newHeader);
        }
        return null;
    }

    private String getTargetUri(HttpServletRequest servletRequest, HttpProxyURI targetURI) {
        return targetURI.getHttpURIString();
    }

    private HttpHost getTargetHost(HttpServletRequest servletRequest, HttpProxyURI targetURI) {
        return targetURI.getHttpHost();
    }

    private String rewriteQueryStringFromRequest(HttpServletRequest servletRequest, String queryString) {
        return queryString;
    }

    // Get the header value as a long in order to more correctly proxy very large requests
    private long getContentLength(HttpServletRequest request) {
        String contentLengthHeader = request.getHeader("Content-Length");
        if (contentLengthHeader != null) {
            return Long.parseLong(contentLengthHeader);
        }
        return -1L;
    }

    /**
     * Take any client cookies that were originally from the proxy and prepare them to send to the
     * proxy.  This relies on cookie headers being set correctly according to RFC 6265 Sec 5.4.
     * This also blocks any local cookies from being sent to the proxy.
     */
    private String getRealCookie(String cookieValue) {
        StringBuilder escapedCookie = new StringBuilder();
        String cookies[] = cookieValue.split("; ");
        for (String cookie : cookies) {
            String cookieSplit[] = cookie.split("=");
            if (cookieSplit.length == 2) {
                String cookieName = cookieSplit[0];
                if (cookieName.startsWith(getCookieNamePrefix(cookieName))) {
                    cookieName = cookieName.substring(getCookieNamePrefix(cookieName).length());
                    if (escapedCookie.length() > 0) {
                        escapedCookie.append("; ");
                    }
                    escapedCookie.append(cookieName).append("=").append(cookieSplit[1]);
                }
            }

            cookieValue = escapedCookie.toString();
        }
        return cookieValue;
    }

    /**
     * The string prefixing rewritten cookies.
     */
    private String getCookieNamePrefix(String name) {
        return "";
    }
}
