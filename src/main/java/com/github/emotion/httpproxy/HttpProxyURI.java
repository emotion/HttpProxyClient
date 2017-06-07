package com.github.emotion.httpproxy;

import org.apache.http.HttpHost;

import java.net.URI;

/**
 * @author emotion
 * @date 19/11/2016
 */
public class HttpProxyURI {
    private String httpURIString;
    private URI httpURIObj;
    private HttpHost httpHost;

    public HttpProxyURI(URI uri) {
        this.httpURIObj = uri;
        this.httpURIString = this.httpURIObj.toString();
        this.httpHost = new HttpHost(uri.getHost());
    }

    public String getHttpURIString() {
        return httpURIString;
    }

    public URI getHttpURIObj() {
        return httpURIObj;
    }

    public HttpHost getHttpHost() {
        return httpHost;
    }

    @Override
    public String toString() {
        return this.httpURIString;
    }
}
