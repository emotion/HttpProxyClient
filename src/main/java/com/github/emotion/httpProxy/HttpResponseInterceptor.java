package com.github.emotion.httpProxy;

import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by emotion on 19/11/2016.
 */
public interface HttpResponseInterceptor {
    void interpret(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpResponse httpResponse, HttpURI targetUri) throws IOException;
}
