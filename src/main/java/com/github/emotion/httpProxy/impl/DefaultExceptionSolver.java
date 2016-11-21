package com.github.emotion.httpProxy.impl;

import com.github.emotion.httpProxy.ExceptionSolver;
import com.github.emotion.httpProxy.HttpURI;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by emotion on 19/11/2016.
 */
public class DefaultExceptionSolver implements ExceptionSolver {
    public void solve(Exception exception, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpUriRequest httpUriRequest, HttpResponse httpResponse, HttpURI targetURI) throws Exception {

    }
}
