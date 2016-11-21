package com.github.emotion.http.proxy;

import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by emotion on 19/11/2016.
 */
public interface HttpProxyResponseInterpreter {
    void interpret(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpResponse httpResponse, HttpProxyURI targetUri) throws IOException;
}
