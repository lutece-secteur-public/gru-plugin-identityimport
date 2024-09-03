package fr.paris.lutece.plugins.identityimport.rbac;

import fr.paris.lutece.plugins.identityimport.business.Client;
import fr.paris.lutece.plugins.identityimport.business.ClientHome;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AccessImportBatchResourceIdService extends ResourceIdService {

    private static final String PLUGIN_NAME = "identityimport";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "identityimport.rbac.access.import.batch.label";
    private static final String PROPERTY_LABEL_READ = "identityimport.rbac.access.import.batch.permission.read";
    private static final String PROPERTY_LABEL_WRITE = "identityimport.rbac.access.import.batch.permission.write";
    private static final String PROPERTY_LABEL_CREATE = "identityimport.rbac.access.import.batch.permission.create";
    private static final String PROPERTY_LABEL_MANUAL_TREATMENT = "identityimport.rbac.access.import.batch.permission.manual.treatment";

    @Override
    public void register() {
        final ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( this.getClass().getName() );
        rt.setPluginName( PLUGIN_NAME );
        rt.setResourceTypeKey( AccessImportBatchResource.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        final Permission permRead = new Permission( );
        permRead.setPermissionKey( AccessImportBatchResource.PERMISSION_READ );
        permRead.setPermissionTitleKey( PROPERTY_LABEL_READ );
        rt.registerPermission( permRead );

        final Permission permWrite = new Permission( );
        permWrite.setPermissionKey( AccessImportBatchResource.PERMISSION_WRITE );
        permWrite.setPermissionTitleKey( PROPERTY_LABEL_WRITE );
        rt.registerPermission( permWrite );

        final Permission permCreate = new Permission( );
        permCreate.setPermissionKey( AccessImportBatchResource.PERMISSION_CREATE );
        permCreate.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( permCreate );

        final Permission permManual = new Permission( );
        permManual.setPermissionKey( AccessImportBatchResource.PERMISSION_MANUAL_TREATMENT );
        permManual.setPermissionTitleKey( PROPERTY_LABEL_MANUAL_TREATMENT );
        rt.registerPermission( permManual );

        ResourceTypeManager.registerResourceType(rt);
    }

    @Override
    public ReferenceList getResourceIdList(final Locale locale) {
        final Map<String, String> importClientCodes =
                ClientHome.getClientsList().stream().map(Client::getClientCode).distinct().collect(Collectors.toMap(Function.identity(), Function.identity()));
        return ReferenceList.convert(importClientCodes);
    }

    @Override
    public String getTitle(final String strClientCode, final Locale locale) {
        return strClientCode;
    }
}
