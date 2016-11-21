package com.github.emotion.http.proxy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by emotion on 19/11/2016.
 */
public class HttpProxyClientTest {
    HttpProxyClient httpProxyClient = HttpProxyClients.create().withTargetUri(new URI("https://www.baidu.com/")).build();
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    HttpServletResponse httpServletResponse;

    public HttpProxyClientTest() throws URISyntaxException {
    }

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        Mockito.when(httpServletRequest.getMethod()).thenReturn("GET");
        Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/");

    }
    @Test
    public void test() throws Exception {
        httpProxyClient.process(httpServletRequest, httpServletResponse);
    }
}