package com.github.emotion.httpproxy.filter;

import com.github.emotion.httpproxy.HttpProxyClient;
import com.github.emotion.httpproxy.HttpProxyClients;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author emotion
 * @date 19/11/2016
 */
public class DefaultHttpProxyFilter implements Filter {
    private HttpProxyClient httpProxyClient = HttpProxyClients.custom().build();

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
