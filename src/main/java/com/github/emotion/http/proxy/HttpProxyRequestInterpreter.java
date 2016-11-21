package com.github.emotion.http.proxy;

import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by emotion on 19/11/2016.
 */
public interface HttpProxyRequestInterpreter {
    HttpUriRequest interpret(HttpServletRequest httpServletRequest, HttpProxyURI targetURI) throws IOException;
}
