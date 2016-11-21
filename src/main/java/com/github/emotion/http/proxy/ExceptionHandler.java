package com.github.emotion.http.proxy;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by emotion on 19/11/2016.
 */
public interface ExceptionHandler {
    void handle(Exception exception, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpUriRequest httpUriRequest, HttpResponse httpResponse, HttpProxyURI targetURI) throws Exception;
}
