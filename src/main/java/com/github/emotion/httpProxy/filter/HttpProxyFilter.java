package com.github.emotion.httpProxy.filter;

import com.github.emotion.httpProxy.HttpProxyClient;
import com.github.emotion.httpProxy.HttpProxyClientBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by emotion on 19/11/2016.
 */
public class HttpProxyFilter implements Filter {
    private HttpProxyClient httpProxyClient = HttpProxyClientBuilder.aHttpProxyClient().build();

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        try {
            httpProxyClient.process(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    public void destroy() {
        httpProxyClient.close();
    }
}
