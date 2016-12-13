package com.github.emotion.http.proxy;

import com.github.emotion.http.proxy.impl.DefaultExceptionHandler;
import com.github.emotion.http.proxy.impl.DefaultHttpClient;
import com.github.emotion.http.proxy.impl.DefaultHttpProxyRequestInterpreter;
import com.github.emotion.http.proxy.impl.DefaultHttpProxyResponseInterpreter;
import org.apache.http.client.HttpClient;

import java.net.URI;

/**
 * Created by emotion on 19/11/2016.
 */
public final class HttpProxyClients {
    private URI                          targetUri;
    private HttpClient                   httpClient;
    private HttpProxyRequestInterpreter  httpProxyRequestInterpreter;
    private HttpProxyResponseInterpreter httpProxyResponseInterpreter;
    private ExceptionHandler             exceptionHandler;

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

    public HttpProxyClients withHttpUriRequestBuilder(HttpProxyRequestInterpreter httpProxyRequestInterpreter) {
        this.httpProxyRequestInterpreter = httpProxyRequestInterpreter;
        return this;
    }

    public HttpProxyClients withHttpResponseInterpretor(HttpProxyResponseInterpreter httpProxyResponseInterpreter) {
        this.httpProxyResponseInterpreter = httpProxyResponseInterpreter;
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
        HttpProxyRequestInterpreter httpProxyRequestInterpreter = this.httpProxyRequestInterpreter != null ? this.httpProxyRequestInterpreter : new DefaultHttpProxyRequestInterpreter();
        HttpProxyResponseInterpreter httpProxyResponseInterpreter = this.httpProxyResponseInterpreter != null ? this.httpProxyResponseInterpreter : new DefaultHttpProxyResponseInterpreter();
        ExceptionHandler exceptionHandler = this.exceptionHandler != null ? this.exceptionHandler : new DefaultExceptionHandler();
        return new HttpProxyClient(targetUri, httpClient, httpProxyRequestInterpreter, httpProxyResponseInterpreter, exceptionHandler);
    }
}
