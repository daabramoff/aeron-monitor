package io.aeron.monitor;

import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class WebServerCustomizer
        implements WebServerFactoryCustomizer<JettyServletWebServerFactory> {

    private static final int DEFAULT_SERVER_ACCEPTORS = 1;

    @Autowired
    private ThreadPool taskExecutor;

    @Override
    public void customize(JettyServletWebServerFactory factory) {
        factory.setThreadPool(taskExecutor);
        factory.setAcceptors(DEFAULT_SERVER_ACCEPTORS);
    }
}
