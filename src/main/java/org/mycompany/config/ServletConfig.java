package org.mycompany.config;

import org.apache.shiro.web.servlet.ShiroFilter;
import org.apache.wicket.protocol.http.WicketFilter;
import org.mycompany.OneDevStarterApp;
import org.mycompany.starter.ServletAppLoader;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import java.io.File;
import java.util.logging.Handler;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;
import com.google.inject.servlet.GuiceFilter;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import io.onedev.commons.launcher.bootstrap.Bootstrap;
import io.onedev.server.git.GitFilter;
import io.onedev.server.git.hookcallback.GitPostReceiveCallback;
import io.onedev.server.git.hookcallback.GitPreReceiveCallback;
import io.onedev.server.web.component.markdown.AttachmentUploadServlet;

@WebListener()
public class ServletConfig implements ServletContextListener {
    
    private final ServletAppLoader appLoader = new ServletAppLoader(); 
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        
        Bootstrap.installDir = new File("/home/andrea/temp");
        configureLogging();
        appLoader.startGiceCI();
        appLoader.loadModules();
        
        FilterRegistration shiroFilter = servletContext.addFilter("shiro", ServletAppLoader.getInstance(ShiroFilter.class));
        shiroFilter.addMappingForUrlPatterns(null, true, "/*");
        
        FilterRegistration gitFilter = servletContext.addFilter("git", ServletAppLoader.getInstance(GitFilter.class));
        gitFilter.addMappingForUrlPatterns(null, true, "/*");
        
        Dynamic preReceiveServlet = servletContext.addServlet("gitPre", ServletAppLoader.getInstance(GitPreReceiveCallback.class));
        preReceiveServlet.addMapping(GitPreReceiveCallback.PATH + "/*");
        
        Dynamic postReceiveServlet = servletContext.addServlet("gitPost", ServletAppLoader.getInstance(GitPostReceiveCallback.class));
        postReceiveServlet.addMapping(GitPostReceiveCallback.PATH + "/*");
        
        Dynamic attachmentUploadServlet = servletContext.addServlet("attachmentUpload", ServletAppLoader.getInstance(AttachmentUploadServlet.class));
        attachmentUploadServlet.addMapping("/attachment_upload");
        
        OneDevStarterApp oneWebApplication = ServletAppLoader.getInstance(OneDevStarterApp.class);
        
        FilterRegistration wicketFilter = servletContext.addFilter("wicket", new WicketFilter(oneWebApplication));
        wicketFilter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        wicketFilter.addMappingForUrlPatterns(null, true, "/*");

        FilterRegistration filterGuice = servletContext.addFilter("guice", ServletAppLoader.getInstance(GuiceFilter.class));
        filterGuice.addMappingForUrlPatterns(null, true, "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        
    }
    
    private static void configureLogging() {
        // Set system properties so that they can be used in logback
       
            System.setProperty("logback.logFile", Bootstrap.installDir.getAbsolutePath() + "/logs/server.log");
            System.setProperty("logback.consoleLogPattern", "%d{HH:mm:ss} %-5level %logger{36} - %msg%n");          
            System.setProperty("logback.fileLogPattern", "%date %-5level [%thread] %logger{36} %msg%n");

        File configFile = new File(Bootstrap.installDir, "conf/logback.xml");
        System.setProperty(Bootstrap.LOGBACK_CONFIG_FILE_PROPERTY_NAME, configFile.getAbsolutePath());

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();
            configurator.doConfigure(configFile);
        } catch (JoranException je) {
            je.printStackTrace();
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        // Redirect JDK logging to slf4j
        java.util.logging.Logger jdkLogger = java.util.logging.Logger.getLogger("");
        for (Handler handler : jdkLogger.getHandlers())
            jdkLogger.removeHandler(handler);
        SLF4JBridgeHandler.install();
    }

    
}
