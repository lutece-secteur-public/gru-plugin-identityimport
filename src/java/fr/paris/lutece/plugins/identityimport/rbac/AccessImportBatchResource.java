package fr.paris.lutece.plugins.identityimport.rbac;

import fr.paris.lutece.portal.service.rbac.RBACResource;

public class AccessImportBatchResource implements RBACResource {

    // RBAC management
    public static final String RESOURCE_TYPE = "ACCESS_IMPORT_BATCH";

    // Perimissions
    public static final String PERMISSION_READ = "READ";
    public static final String PERMISSION_WRITE = "WRITE";
    public static final String PERMISSION_CREATE = "CREATE";
    public static final String PERMISSION_MANUAL_TREATMENT = "MANUAL_TREATMENT";

    @Override
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    @Override
    public String getResourceId( )
    {
        return null;
    }
}
