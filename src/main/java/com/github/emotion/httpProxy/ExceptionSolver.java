package com.github.emotion.httpProxy;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by emotion on 19/11/2016.
 */
public interface ExceptionSolver {
    void solve(Exception exception, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpUriRequest httpUriRequest, HttpResponse httpResponse, HttpURI targetURI) throws Exception;
}
