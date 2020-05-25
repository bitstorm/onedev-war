package org.mycompany.config;

import org.apache.shiro.web.servlet.ShiroFilter;
import org.apache.wicket.protocol.http.WicketFilter;
import org.mycompany.OneDevStarterApp;
import org.mycompany.starter.ServletAppLoader;
import java.io.File;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.WebListener;
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
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        
    }
    
}
