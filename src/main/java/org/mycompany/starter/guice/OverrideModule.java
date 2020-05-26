package org.mycompany.starter.guice;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.mycompany.OneDevStarterApp;
import org.mycompany.config.mock.JettyLauncherMock;
import io.onedev.commons.launcher.loader.AbstractPluginModule;
import io.onedev.server.util.jetty.JettyLauncher;

public class OverrideModule extends AbstractPluginModule {
    @Override
    protected void configure() {
        super.configure();
        bind(OneDevStarterApp.class);
        
        bind(JettyLauncher.class).to(JettyLauncherMock.class);
        bind(ServletContextHandler.class).toProvider(JettyLauncherMock.class);
    }
}
