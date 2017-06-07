package com.github.emotion.httpproxy.handler;

import com.github.emotion.httpproxy.HttpProxyURI;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author emotion
 * @date 19/11/2016
 */
public interface HttpProxyRequestHandler {
    HttpUriRequest interpret(HttpServletRequest httpServletRequest, HttpProxyURI targetURI) throws IOException;
}
