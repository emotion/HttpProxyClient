package com.github.emotion.httpproxy.handler;

import com.github.emotion.httpproxy.HttpProxyURI;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author emotion
 * @date 19/11/2016
 */
public interface ExceptionHandler {
    void handle(Exception exception, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpUriRequest httpUriRequest, HttpResponse httpResponse, HttpProxyURI targetURI) throws Exception;
}
