package org.mycompany;

import org.apache.wicket.ISessionListener;
import org.apache.wicket.Session;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.mycompany.resource.DynamicPackageResource;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import io.onedev.server.web.OneWebApplication;
import io.onedev.server.web.WebApplicationConfigurator;
import io.onedev.server.web.img.Img;
import io.onedev.server.web.page.layout.UICustomization;
import io.onedev.server.web.websocket.WebSocketManager;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see org.mycompany.Start#main(String[])
 */
@Singleton
public class OneDevStarterApp extends OneWebApplication
{

    private WebSocketManager webSocketManager;

    @Inject
	public OneDevStarterApp(Set<WebApplicationConfigurator> applicationConfigurators,
            UICustomization uiCustomization, WebSocketManager webSocketManager) {
        super(applicationConfigurators, uiCustomization);
        this.webSocketManager = webSocketManager;
    }

    /**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();

		getSessionListeners().add(new ISessionListener() {
            
            @Override
            public void onUnbound(String sessionId) {
                webSocketManager.onDestroySession(sessionId);
            }
            
            @Override
            public void onCreated(Session session) {
            }
        });
		mountResource("/img", new ResourceReference(Img.class, "imgLoader") {

            @Override
            public IResource getResource() {
                Request request = RequestCycle.get().getRequest();
                Url clientUrl = request.getClientUrl();
                List<String> segments = clientUrl.getSegments();
                
                return new DynamicPackageResource(getScope(), segments.get(segments.size() - 1));
            }
		    
		});
	}
}
