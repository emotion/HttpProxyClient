package com.github.emotion.http.proxy;

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
public final class HttpProxyClient {
    private HttpProxyURI                 targetURI;
    private HttpClient                   httpClient;
    private HttpProxyRequestInterpreter  httpProxyRequestInterpreter;
    private HttpProxyResponseInterpreter httpProxyResponseInterpreter;
    private ExceptionHandler             exceptionHandler;

    public HttpProxyClient(URI targetURI, HttpClient httpClient, HttpProxyRequestInterpreter httpProxyRequestInterpreter
            , HttpProxyResponseInterpreter httpProxyResponseInterpreter, ExceptionHandler exceptionHandler) {
        if (targetURI == null) {
            throw new IllegalArgumentException("targetURI must be non null");
        }
        this.targetURI = new HttpProxyURI(targetURI);
        this.httpClient = httpClient;
        this.httpProxyRequestInterpreter = httpProxyRequestInterpreter;
        this.httpProxyResponseInterpreter = httpProxyResponseInterpreter;
        this.exceptionHandler = exceptionHandler;
    }

    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        HttpResponse httpResponse = null;
        HttpUriRequest httpUriRequest = null;
        try {
            httpUriRequest = httpProxyRequestInterpreter.build(httpServletRequest, targetURI);
            if (httpUriRequest == null) {
                httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            httpResponse = httpClient.execute(httpUriRequest);
            httpProxyResponseInterpreter.interpret(httpServletRequest, httpServletResponse, httpResponse, targetURI);
        } catch (Exception e) {
            exceptionHandler.handle(e, httpServletRequest, httpServletResponse, httpUriRequest, httpResponse, targetURI);
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
