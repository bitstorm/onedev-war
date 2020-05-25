package org.mycompany.starter.guice;

import org.mycompany.OneDevStarterApp;
import io.onedev.commons.launcher.loader.AbstractPluginModule;

public class OverrideModule extends AbstractPluginModule {
    @Override
    protected void configure() {
        super.configure();
        bind(OneDevStarterApp.class);
    }
}
