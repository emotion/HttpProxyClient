package com.github.emotion.httpProxy;

import com.github.emotion.httpProxy.impl.DefaultExceptionSolver;
import com.github.emotion.httpProxy.impl.DefaultHttpClient;
import com.github.emotion.httpProxy.impl.DefaultHttpResponseInterceptor;
import com.github.emotion.httpProxy.impl.DefaultHttpUriRequestBuilder;
import org.apache.http.client.HttpClient;

import java.net.URI;

/**
 * Created by emotion on 19/11/2016.
 */
public final class HttpProxyClientBuilder {
    private URI                     targetUri;
    private HttpClient              httpClient;
    private HttpUriRequestBuilder   httpUriRequestBuilder;
    private HttpResponseInterceptor httpResponseInterceptor;
    private ExceptionSolver         exceptionSolver;

    private HttpProxyClientBuilder() {
    }

    public static HttpProxyClientBuilder aHttpProxyClient() {
        return new HttpProxyClientBuilder();
    }

    public HttpProxyClientBuilder withTargetUri(URI targetUri) {
        this.targetUri = targetUri;
        return this;
    }

    public HttpProxyClientBuilder withHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public HttpProxyClientBuilder withHttpUriRequestBuilder(HttpUriRequestBuilder httpUriRequestBuilder) {
        this.httpUriRequestBuilder = httpUriRequestBuilder;
        return this;
    }

    public HttpProxyClientBuilder withHttpResponseInterceptor(HttpResponseInterceptor httpResponseInterceptor) {
        this.httpResponseInterceptor = httpResponseInterceptor;
        return this;
    }

    public HttpProxyClientBuilder withHttpExceptionSolver(ExceptionSolver exceptionSolver) {
        this.exceptionSolver = exceptionSolver;
        return this;
    }

    public HttpProxyClient build() {
        if (targetUri == null) {
            throw new IllegalArgumentException("targetUri must be non null");
        }
        URI targetUri = this.targetUri;
        HttpClient httpClient = this.httpClient != null ? this.httpClient : new DefaultHttpClient();
        HttpUriRequestBuilder httpUriRequestBuilder = this.httpUriRequestBuilder != null ? this.httpUriRequestBuilder : new DefaultHttpUriRequestBuilder();
        HttpResponseInterceptor httpResponseInterceptor = this.httpResponseInterceptor != null ? this.httpResponseInterceptor : new DefaultHttpResponseInterceptor();
        ExceptionSolver exceptionSolver = this.exceptionSolver != null ? this.exceptionSolver : new DefaultExceptionSolver();
        return new HttpProxyClient(targetUri, httpClient, httpUriRequestBuilder, httpResponseInterceptor, exceptionSolver);
    }
}
