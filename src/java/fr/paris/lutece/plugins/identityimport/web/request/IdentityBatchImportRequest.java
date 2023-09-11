package fr.paris.lutece.plugins.identityimport.web.request;

import fr.paris.lutece.plugins.identityimport.service.BatchService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportResponse;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;

public class IdentityBatchImportRequest extends AbstractRequest {
    protected BatchImportRequest _request;
    public IdentityBatchImportRequest(final BatchImportRequest request, final String strClientCode) {
        super(strClientCode);
        this._request = request;
    }

    @Override
    protected void validRequest() throws IdentityStoreException {

    }

    @Override
    protected BatchImportResponse doSpecificRequest() throws IdentityStoreException {
        BatchService.instance().importBatch(_request.getBatch(), null, null);
        return null;
    }
}
