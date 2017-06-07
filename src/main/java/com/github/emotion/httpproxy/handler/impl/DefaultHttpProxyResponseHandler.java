package com.github.emotion.httpproxy.handler.impl;

import com.github.emotion.httpproxy.handler.HttpProxyResponseHandler;
import com.github.emotion.httpproxy.HttpProxyURI;
import com.github.emotion.httpproxy.utils.HttpHeaderUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.List;

/**
 * @author emotion
 * @date 19/11/2016
 */
public class DefaultHttpProxyResponseHandler implements HttpProxyResponseHandler {

    public void interpret(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpResponse httpResponse, HttpProxyURI targetUri) throws IOException {
        // Process the response:

        // Pass the response code. This method with the "reason phrase" is deprecated but it's the
        //   only way to pass the reason along too.
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        //noinspection deprecation
        httpServletResponse.setStatus(statusCode, httpResponse.getStatusLine().getReasonPhrase());

        // Copying response headers to make sure SESSIONID or other Cookie which comes from the remote
        // server will be saved in client when the proxied url was redirected to another one.
        // See issue [#51](https://github.com/mitre/HTTP-Proxy-Servlet/issues/51)
        copyResponseHeaders(httpResponse, httpServletRequest, httpServletResponse, targetUri);
        if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
            // 304 needs special handling.  See:
            // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
            // Don't send body entity/content!
            httpServletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
        } else {
            // Send the content to the client
            copyResponseEntity(httpResponse, httpServletResponse, httpServletRequest);
        }
    }

    /**
     * Copy proxied response headers back to the servlet client.
     */
    private void copyResponseHeaders(HttpResponse proxyResponse, HttpServletRequest servletRequest,
                                     HttpServletResponse servletResponse, HttpProxyURI targetUri) {
        for (Header header : proxyResponse.getAllHeaders()) {
            copyResponseHeader(servletRequest, servletResponse, header, targetUri);
        }
    }

    /**
     * Copy a proxied response header back to the servlet client.
     * This is easily overwritten to filter out certain headers if desired.
     */
    private void copyResponseHeader(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse, Header header, HttpProxyURI targetUri) {
        String headerName = header.getName();
        if (HttpHeaderUtils.isHopByHopHeader(headerName))
            return;
        String headerValue = header.getValue();
        if (headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE) ||
                headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE2)) {
            copyProxyCookie(servletRequest, servletResponse, headerValue);
        } else if (headerName.equalsIgnoreCase(HttpHeaders.LOCATION)) {
            // LOCATION Header may have to be rewritten.
            servletResponse.addHeader(headerName, rewriteUrlFromResponse(servletRequest, headerValue, targetUri));
        } else {
            servletResponse.addHeader(headerName, headerValue);
        }
    }

    /**
     * Copy cookie from the proxy to the servlet client.
     * Replaces cookie path to local path and renames cookie to avoid collisions.
     */
    private void copyProxyCookie(HttpServletRequest servletRequest,
                                 HttpServletResponse servletResponse, String headerValue) {
        List<HttpCookie> cookies = HttpCookie.parse(headerValue);
        String path = servletRequest.getContextPath(); // path starts with / or is empty string
        path += servletRequest.getServletPath(); // servlet path starts with / or is empty string

        for (HttpCookie cookie : cookies) {
            //set cookie name prefixed w/ a proxy value so it won't collide w/ other cookies
            String proxyCookieName = getCookieNamePrefix(cookie.getName()) + cookie.getName();
            Cookie servletCookie = new Cookie(proxyCookieName, cookie.getValue());
            servletCookie.setComment(cookie.getComment());
            servletCookie.setMaxAge((int) cookie.getMaxAge());
            servletCookie.setPath(path); //set to the path of the proxy servlet
            // don't set cookie domain
            servletCookie.setSecure(cookie.getSecure());
            servletCookie.setVersion(cookie.getVersion());
            servletResponse.addCookie(servletCookie);
        }
    }

    /**
     * The string prefixing rewritten cookies.
     */
    private String getCookieNamePrefix(String name) {
        return "";
    }


    /**
     * Copy response body data (the entity) from the proxy to the servlet client.
     */
    private void copyResponseEntity(HttpResponse proxyResponse, HttpServletResponse servletResponse, HttpServletRequest servletRequest) throws IOException {
        HttpEntity entity = proxyResponse.getEntity();
        if (entity != null) {
            OutputStream servletOutputStream = servletResponse.getOutputStream();
            entity.writeTo(servletOutputStream);
        }
    }


    /**
     * For a redirect response from the target server, this translates {@code theUrl} to redirect to
     * and translates it to one the original client can use.
     */
    private String rewriteUrlFromResponse(HttpServletRequest servletRequest, String theUrl, HttpProxyURI targetUri) {
        //TODO document example paths
        final String targetUriStr = getTargetUri(servletRequest, targetUri);
        if (theUrl.startsWith(targetUriStr)) {
      /*-
       * The URL points back to the back-end server.
       * Instead of returning it verbatim we replace the target path with our
       * source path in a way that should instruct the original client to
       * request the URL pointed through this Proxy.
       * We do this by taking the current request and rewriting the path part
       * using this servlet's absolute path and the path from the returned URL
       * after the base target URL.
       */
            StringBuffer curUrl = servletRequest.getRequestURL();//no query
            int pos;
            // Skip the protocol part
            if ((pos = curUrl.indexOf("://")) >= 0) {
                // Skip the authority part
                // + 3 to skip the separator between protocol and authority
                if ((pos = curUrl.indexOf("/", pos + 3)) >= 0) {
                    // Trim everything after the authority part.
                    curUrl.setLength(pos + 1);
                }
            }
            // Context path starts with a / if it is not blank
            curUrl.append(servletRequest.getContextPath());
            // Servlet path starts with a / if it is not blank
            curUrl.append(servletRequest.getServletPath());
            curUrl.append(theUrl, targetUriStr.length(), theUrl.length());
            theUrl = curUrl.toString();
        }
        return theUrl;
    }

    private String getTargetUri(HttpServletRequest servletRequest, HttpProxyURI targetUri) {
        return targetUri.getHttpURIString();
    }

}
