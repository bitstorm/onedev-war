package org.mycompany.config.mock;

import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import javax.inject.Provider;
import io.onedev.server.util.jetty.JettyLauncher;

public class JettyLauncherMock implements JettyLauncher, Provider<ServletContextHandler> {
    private final ServletContextHandler mockContext;
    
    public JettyLauncherMock() {
        mockContext = new ServletContextHandler();
        SessionHandler sessionHandler = new SessionHandler();
        mockContext.setSessionHandler(sessionHandler);
        sessionHandler.setMaxInactiveInterval(180);
    }

    @Override
    public ServletContextHandler get() {
        
        return mockContext;
    }

    @Override
    public void start() {
        System.out.println();
        
    }

    @Override
    public void stop() {
        
    }

}
