/*
 * Copyright (c) 2002-2024, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.identityimport.web.request;

import fr.paris.lutece.plugins.identityimport.service.BatchService;
import fr.paris.lutece.plugins.identityimport.service.BatchValidationService;
import fr.paris.lutece.plugins.identityimport.service.ServiceContractService;
import fr.paris.lutece.plugins.identityimport.web.validator.BatchRequestValidator;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.AbstractIdentityStoreRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.ClientAuthorizationException;
import fr.paris.lutece.plugins.identitystore.web.exception.DuplicatesConsistencyException;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.identitystore.web.exception.RequestContentFormattingException;
import fr.paris.lutece.plugins.identitystore.web.exception.RequestFormatException;
import fr.paris.lutece.plugins.identitystore.web.exception.ResourceConsistencyException;
import fr.paris.lutece.plugins.identitystore.web.exception.ResourceNotFoundException;

public class IdentityBatchImportRequest extends AbstractIdentityStoreRequest
{
    private final BatchImportRequest _request;
    private final String _strHeaderAppCode;

    private ServiceContractDto serviceContract;

    public IdentityBatchImportRequest( final BatchImportRequest request, final String strHeaderAppCode, final String strClientCode,
            final String strAuthorName, final String strAuthorType ) throws IdentityStoreException
    {
        super( strClientCode, strAuthorName, strAuthorType );
        this._request = request;
        this._strHeaderAppCode = strHeaderAppCode;
    }

    @Override
    protected void fetchResources() throws ResourceNotFoundException {
        serviceContract = ServiceContractService.instance().getActiveServiceContract(_strClientCode);
    }

    @Override
    protected void validateRequestFormat() throws RequestFormatException {
        BatchRequestValidator.instance().checkImportRequest(_request);
        BatchRequestValidator.instance().checkAppAndClientCode(_strHeaderAppCode, _strClientCode);

        BatchValidationService.instance().validateImportBatchLimit(_request.getBatch());
        BatchValidationService.instance().validateUser(_request.getBatch());
        BatchValidationService.instance().validateIdentities(_request.getBatch());
    }

    @Override
    protected void validateClientAuthorization() throws ClientAuthorizationException {
        ServiceContractService.instance().validateImportAuthorization(serviceContract);
        for (final IdentityDto identity : _request.getBatch().getIdentities()) {
            ServiceContractService.instance().validateIdentityAgainstServiceContract(identity, serviceContract);
        }
    }

    @Override
    protected void validateResourcesConsistency() throws ResourceConsistencyException {
        // do nothing
    }

    @Override
    protected void formatRequestContent() throws RequestContentFormattingException {
        // do nothing
    }

    @Override
    protected void checkDuplicatesConsistency() throws DuplicatesConsistencyException {
        BatchValidationService.instance().validateIdentitiesUniqueness(_request.getBatch());
    }

    @Override
    protected BatchImportResponse doSpecificRequest( ) throws IdentityStoreException
    {
        final BatchImportResponse response = new BatchImportResponse( );

        final String batchReference = BatchService.instance().importBatchFromApi(_request, serviceContract.getClientCode());
        response.setReference(batchReference);
        response.setStatus( ResponseStatusFactory.success( ).setMessageKey( Constants.PROPERTY_REST_INFO_SUCCESSFUL_OPERATION ) );

        return response;
    }
}
