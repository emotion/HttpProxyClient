package com.github.emotion.httpProxy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

/**
 * Created by emotion on 18/11/2016.
 */
public class HttpProxyClient {
    private HttpURI                 targetURI;
    private HttpClient              httpClient;
    private HttpUriRequestBuilder   httpUriRequestBuilder;
    private HttpResponseInterceptor httpResponseInterceptor;
    private ExceptionSolver         exceptionSolver;

    public HttpProxyClient(URI targetURI, HttpClient httpClient, HttpUriRequestBuilder httpUriRequestBuilder
            , HttpResponseInterceptor httpResponseInterceptor, ExceptionSolver exceptionSolver) {
        if (targetURI == null) {
            throw new IllegalArgumentException("targetURI must be non null");
        }
        this.targetURI = new HttpURI(targetURI);
        this.httpClient = httpClient;
        this.httpUriRequestBuilder = httpUriRequestBuilder;
        this.httpResponseInterceptor = httpResponseInterceptor;
        this.exceptionSolver = exceptionSolver;
    }

    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        HttpResponse httpResponse = null;
        HttpUriRequest httpUriRequest = null;
        try {
            httpUriRequest = httpUriRequestBuilder.interpret(httpServletRequest, targetURI);
            if (httpUriRequest == null) {
                httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            httpResponse = httpClient.execute(httpUriRequest);
            httpResponseInterceptor.interpret(httpServletRequest, httpServletResponse, httpResponse, targetURI);
        } catch (Exception e) {
            exceptionSolver.solve(e, httpServletRequest, httpServletResponse, httpUriRequest, httpResponse, targetURI);
        } finally {
            if (httpResponse != null) {
                consumeQuietly(httpResponse.getEntity());
            }
            if (httpUriRequest != null && !httpUriRequest.isAborted()) {
                httpUriRequest.abort();
            }
        }
    }

    /**
     * HttpClient v4.1 doesn't have the
     * {@link org.apache.http.util.EntityUtils#consumeQuietly(org.apache.http.HttpEntity)} method.
     */
    private void consumeQuietly(HttpEntity entity) {
        try {
            EntityUtils.consume(entity);
        } catch (IOException e) {//ignore
//            log(e.getMessage(), e);
        }
    }

    public void close() {
        //As of HttpComponents v4.3, clients implement closeable
        if (httpClient instanceof Closeable) {
            try {
                ((Closeable) httpClient).close();
            } catch (IOException e) {
            }
        } else {
            //Older releases require we do this:
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
    }

}
