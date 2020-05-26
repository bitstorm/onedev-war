package org.mycompany.config.mock;

import org.eclipse.jetty.servlet.ServletContextHandler;
import javax.inject.Provider;
import io.onedev.server.util.jetty.JettyLauncher;

public class JettyLauncherMock implements JettyLauncher, Provider<ServletContextHandler> {
    private static ServletContextHandler INSTANCE = new ServletContextHandler();
    @Override
    public ServletContextHandler get() {
        
        return INSTANCE;
    }

    @Override
    public void start() {
        System.out.println();
        
    }

    @Override
    public void stop() {
        
    }

}
