package org.apache.hadoop.http;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;

public class TestHttpServer2 {

    private ServletContext servletContext;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Configuration conf;

    @Before
    public void setup() {
        servletContext = mock(ServletContext.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        conf = new Configuration();

            // When servletContext.getAttribute("hadoop.conf") is called, return the conf object
            when(servletContext.getAttribute("hadoop.conf")).thenReturn(conf);
  
    }

    @Test
    public void testInstrumentationAccessAllowed() throws Exception {
        // Assuming HttpServer2 is modified to accept Configuration in its method or is otherwise accessible.
        HttpServer2 httpServer2 = new HttpServer2.Builder().setName("test")
                                      .addEndpoint(new URI("http://localhost")).build();
        
        // When HADOOP_SECURITY_INSTRUMENTATION_REQUIRES_ADMIN is true, access should be restricted.
        conf.setBoolean("hadoop.security.instrumentation.requires.admin", true);
        httpServer2.setAttribute("conf", conf);
        boolean accessAllowed = HttpServer2.isInstrumentationAccessAllowed(servletContext, request, response);
        assertFalse(accessAllowed);
        
        // When HADOOP_SECURITY_INSTRUMENTATION_REQUIRES_ADMIN is false, access should be allowed.
        conf.setBoolean("hadoop.security.instrumentation.requires.admin", false);
        httpServer2.setAttribute("conf", conf);
        accessAllowed = HttpServer2.isInstrumentationAccessAllowed(servletContext, request, response);
        assertTrue(accessAllowed);
    }
}
