package com.github.emotion.http.proxy.impl;

import com.github.emotion.http.proxy.ExceptionHandler;
import com.github.emotion.http.proxy.HttpProxyURI;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by emotion on 19/11/2016.
 */
public class DefaultExceptionHandler implements ExceptionHandler {
    public void handle(Exception exception, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpUriRequest httpUriRequest, HttpResponse httpResponse, HttpProxyURI targetURI) throws Exception {

    }
}
