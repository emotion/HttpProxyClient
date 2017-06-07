package com.github.emotion.httpproxy;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by emotion on 19/11/2016.
 */
public class HttpProxyClientTest {
    HttpProxyClient httpProxyClient = HttpProxyClients.custom().withTargetUri(new URI("http://www.baidu.com/")).build();

    public HttpProxyClientTest() throws URISyntaxException {
    }

    @Test
    public void test() throws Exception {
        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(httpServletRequest.getMethod()).thenReturn("GET");
        Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/");
        PrintWriter printWriter = new PrintWriter(System.out);
        Mockito.when(httpServletResponse.getWriter()).thenReturn(printWriter);
        httpProxyClient.process(httpServletRequest, httpServletResponse);
        printWriter.flush();
    }
}