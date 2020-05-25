package org.mycompany.resource;

import org.apache.wicket.request.resource.PackageResource;

public class DynamicPackageResource extends PackageResource {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DynamicPackageResource(Class<?> scope, String name) {
        super(scope, name, null, null, null);
    }

}
