package fr.paris.lutece.plugins.identityimport.web.validator;

import fr.paris.lutece.plugins.identityimport.service.ImportClientService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.identitystore.web.exception.RequestFormatException;
import org.apache.commons.lang3.StringUtils;

public class BatchRequestValidator {

    private static final String PROPERTY_ERROR_IMCOMPLETE_BATCH_STATUS_REQUEST = "identityimport.error.incomplete.batch.status.request";
    private static final String PROPERTY_ERROR_PROVIDED_IMPORT_REQUEST_NULL = "identityimport.error.provided.import.request.null";
    private static final String PROPERTY_ERROR_PROVIDED_BATCH_EMPTY = "identityimport.error.provided.batch.empty";
    private static final String PROPERTY_ERROR_CREATE_BATCH_WITH_REFERENCE = "identityimport.error.create.batch.with.reference";
    private static final String PROPERTY_ERROR_MUST_PROVIDE_CLIENT_CODE_OR_TOKEN = "identityimport.error.must.provide.client.code.or.token";

    /**
     * singleton
     */
    private static BatchRequestValidator _singleton;

    /**
     * return singleton's instance
     *
     * @return IdentityRequestValidator
     */
    public static BatchRequestValidator instance() {
        if (_singleton == null) {
            _singleton = new BatchRequestValidator();
        }

        return _singleton;
    }

    public void checkImportRequest(final BatchImportRequest request) throws RequestFormatException {
        if (request == null) {
            throw new RequestFormatException("Provided import request is null.", PROPERTY_ERROR_PROVIDED_IMPORT_REQUEST_NULL);
        }
        if (request.getBatch() != null && StringUtils.isNotBlank(request.getBatch().getReference())) {
            throw new RequestFormatException("Providing a reference when creating a batch is forbidden.", PROPERTY_ERROR_CREATE_BATCH_WITH_REFERENCE);
        }
    }

    public void checkBatchStatusRequest(final BatchStatusRequest request) throws RequestFormatException {
        if (request == null || StringUtils.isBlank(request.getBatchReference()) || request.getMode() == null) {
            throw new RequestFormatException("Invalid batch status request. All fields must be provided.", PROPERTY_ERROR_IMCOMPLETE_BATCH_STATUS_REQUEST);
        }
    }

    public void checkAppAndClientCode(final String strHeaderAppCode, final String _strClientCode) throws RequestFormatException {
        try {
            //TODO refactor si besoin selon le principe du refactoring refactoré
            ImportClientService.instance().getClient(strHeaderAppCode, _strClientCode);
        }
        catch ( final IdentityStoreException e )
        {
            final String message = String.format("You must provide valid headers %s and %s.", Constants.PARAM_CLIENT_CODE, Constants.PARAM_APPLICATION_CODE);
            throw new RequestFormatException(message, PROPERTY_ERROR_MUST_PROVIDE_CLIENT_CODE_OR_TOKEN);
        }
    }
}