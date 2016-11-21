package com.github.emotion.httpProxy.servlet;

import com.github.emotion.httpProxy.HttpProxyClient;
import com.github.emotion.httpProxy.HttpProxyClientBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by emotion on 19/11/2016.
 */
public class HttpProxyServlet implements Servlet {
    private HttpProxyClient httpProxyClient = HttpProxyClientBuilder.aHttpProxyClient().build();

    public void init(ServletConfig servletConfig) throws ServletException {

    }

    public ServletConfig getServletConfig() {
        return null;
    }

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        try {
            httpProxyClient.process(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    public String getServletInfo() {
        return null;
    }

    public void destroy() {
        httpProxyClient.close();
    }
}
