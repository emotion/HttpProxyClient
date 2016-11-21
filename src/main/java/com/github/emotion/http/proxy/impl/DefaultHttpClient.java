package com.github.emotion.http.proxy.impl;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultClientConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by emotion on 19/11/2016.
 */
public class DefaultHttpClient implements HttpClient, Closeable {
    private CloseableHttpClient closeableHttpClient;

    public DefaultHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        ConnectionReuseStrategy connectionReuseStrategy = new DefaultClientConnectionReuseStrategy();
        closeableHttpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setConnectionReuseStrategy(connectionReuseStrategy)
                .disableRedirectHandling()
                .disableCookieManagement()
                .build();
    }

    public CloseableHttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        return closeableHttpClient.execute(target, request, context);
    }

    public CloseableHttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
        return closeableHttpClient.execute(request, context);
    }

    public CloseableHttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        return closeableHttpClient.execute(request);
    }

    public CloseableHttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
        return closeableHttpClient.execute(target, request);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return closeableHttpClient.execute(request, responseHandler);
    }

    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        return closeableHttpClient.execute(request, responseHandler, context);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return closeableHttpClient.execute(target, request, responseHandler);
    }

    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        return closeableHttpClient.execute(target, request, responseHandler, context);
    }

    @Deprecated
    public HttpParams getParams() {
        return closeableHttpClient.getParams();
    }

    @Deprecated
    public ClientConnectionManager getConnectionManager() {
        return closeableHttpClient.getConnectionManager();
    }

    public void close() throws IOException {
        closeableHttpClient.close();
    }
}
