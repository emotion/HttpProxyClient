package com.github.emotion.httpProxy;

import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by emotion on 19/11/2016.
 */
public interface HttpUriRequestBuilder {
    HttpUriRequest interpret(HttpServletRequest httpServletRequest, HttpURI targetURI) throws IOException;
}
