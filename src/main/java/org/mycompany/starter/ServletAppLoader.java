package org.mycompany.starter;

import org.mycompany.starter.guice.OverrideModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.util.Modules;
import com.google.inject.util.Modules.OverriddenModuleBuilder;
import io.onedev.commons.launcher.loader.AbstractPluginModule;
import io.onedev.commons.launcher.loader.AppLoader;
import io.onedev.commons.launcher.loader.AppLoaderModule;
import io.onedev.commons.launcher.loader.PluginManager;
import io.onedev.commons.utils.DependencyUtils;
import io.onedev.commons.utils.FileUtils;
import io.onedev.commons.utils.StringUtils;

public class ServletAppLoader extends AppLoader {
    private static final String OVERRIDE_MODULE = "overrideModule";
    private static final Logger logger = LoggerFactory.getLogger(ServletAppLoader.class);
    
    public void startGiceCI() {
        logger.info("Initializing dependency injection container...");
        
        OverriddenModuleBuilder builder = Modules.override(new AppLoaderModule());
        
        Map<String, AbstractPluginModule> modules = loadPluginModules();
        modules.put(OVERRIDE_MODULE, new OverrideModule());
        
        List<String> sortDependencies = DependencyUtils.sortDependencies(modules);
        sortDependencies.remove(OVERRIDE_MODULE);
        sortDependencies.add(OVERRIDE_MODULE);
        
        for (String key: sortDependencies) {
            builder = Modules.override(builder.with(modules.get(key)));
        }
        
        injector = Guice.createInjector(builder.with(new AbstractModule() {

            @Override
            protected void configure() {
            }
            
        }));
        
    }
    
    public void loadModules() {
        logger.info("Starting plugin manager...");
        injector.getInstance(PluginManager.class).start();
    }
    
    private Map<String, AbstractPluginModule> loadPluginModules() {
        Map<String, AbstractPluginModule> pluginModules = new HashMap<String, AbstractPluginModule>();
        
        URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        for (URL url: classLoader.getURLs()) {
            String path;
            try {
                path = url.toURI().getPath();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            Properties pluginProps = FileUtils.loadProperties(new File(path), "META-INF/onedev-plugin.properties");
            if (pluginProps != null) {
                Properties productProps = FileUtils.loadProperties(new File(path), "META-INF/onedev-product.properties");
                String pluginId = pluginProps.getProperty("id");
                if (pluginModules.containsKey(pluginId))
                    throw new RuntimeException("More than one version of plugin '" + pluginId + "' is found.");
                
                String moduleClassName = pluginProps.getProperty("module");
                try {
                    Class<?> moduleClass = Class.forName(moduleClassName);
                    
                    if (AbstractPluginModule.class.isAssignableFrom(moduleClass)) {
                        AbstractPluginModule pluginModule = (AbstractPluginModule) moduleClass.newInstance();
                        
                        pluginModule.setPluginId(pluginId);
                        pluginModule.setPluginName(pluginProps.getProperty("name"));
                        pluginModule.setPluginDescription(pluginProps.getProperty("description"));
                        pluginModule.setPluginVendor(pluginProps.getProperty("vendor"));
                        pluginModule.setPluginVersion(pluginProps.getProperty("version"));
                        pluginModule.setProduct(productProps != null);
                        String dependenciesStr = pluginProps.getProperty("dependencies");
                        if (dependenciesStr != null)
                            pluginModule.setPluginDependencies(new HashSet<String>(StringUtils.splitAndTrim(dependenciesStr, ";")));
                        pluginModules.put(pluginId, pluginModule);
                    } else {
                        throw new RuntimeException("Plugin module class should extend from '" 
                                + AbstractPluginModule.class.getName() + "'.");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error loading plugin '" + pluginId + "'.", e);
                }
            }
        }
        
        return pluginModules;
    }
}
