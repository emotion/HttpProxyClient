package com.github.emotion.httpproxy;

import com.github.emotion.httpproxy.handler.ExceptionHandler;
import com.github.emotion.httpproxy.handler.impl.DefaultExceptionHandler;
import com.github.emotion.httpproxy.handler.impl.DefaultHttpProxyRequestHandler;
import com.github.emotion.httpproxy.handler.impl.DefaultHttpProxyResponseHandler;
import com.github.emotion.httpproxy.handler.HttpProxyRequestHandler;
import com.github.emotion.httpproxy.handler.HttpProxyResponseHandler;
import org.apache.http.client.HttpClient;

import java.net.URI;

/**
 * @author emotion
 * @date 19/11/2016
 */
public final class HttpProxyClients {
    private URI targetUri;
    private HttpClient httpClient;
    private HttpProxyRequestHandler httpProxyRequestHandler;
    private HttpProxyResponseHandler httpProxyResponseHandler;
    private ExceptionHandler exceptionHandler;

    private HttpProxyClients() {
    }

    public static HttpProxyClients custom() {
        return new HttpProxyClients();
    }

    public HttpProxyClients withTargetUri(URI targetUri) {
        this.targetUri = targetUri;
        return this;
    }

    public HttpProxyClients withHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public HttpProxyClients withHttpUriRequestBuilder(HttpProxyRequestHandler httpProxyRequestHandler) {
        this.httpProxyRequestHandler = httpProxyRequestHandler;
        return this;
    }

    public HttpProxyClients withHttpResponseInterpretor(HttpProxyResponseHandler httpProxyResponseHandler) {
        this.httpProxyResponseHandler = httpProxyResponseHandler;
        return this;
    }

    public HttpProxyClients withHttpExceptionSolver(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public HttpProxyClient build() {
        if (targetUri == null) {
            throw new IllegalArgumentException("targetUri must be non null");
        }
        URI targetUri = this.targetUri;
        HttpClient httpClient = this.httpClient != null ? this.httpClient : new DefaultHttpClient();
        HttpProxyRequestHandler httpProxyRequestHandler = this.httpProxyRequestHandler != null ? this.httpProxyRequestHandler : new DefaultHttpProxyRequestHandler();
        HttpProxyResponseHandler httpProxyResponseHandler = this.httpProxyResponseHandler != null ? this.httpProxyResponseHandler : new DefaultHttpProxyResponseHandler();
        ExceptionHandler exceptionHandler = this.exceptionHandler != null ? this.exceptionHandler : new DefaultExceptionHandler();
        return new HttpProxyClient(targetUri, httpClient, httpProxyRequestHandler, httpProxyResponseHandler, exceptionHandler);
    }
}
