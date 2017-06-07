package com.github.emotion.httpproxy.handler;

import com.github.emotion.httpproxy.HttpProxyURI;
import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author emotion
 * @date 19/11/2016
 */
public interface HttpProxyResponseHandler {
    void interpret(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpResponse httpResponse, HttpProxyURI targetUri) throws IOException;
}
