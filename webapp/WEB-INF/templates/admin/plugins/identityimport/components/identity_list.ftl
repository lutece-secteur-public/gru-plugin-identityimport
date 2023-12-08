<div class="lutece-compare-item-container border-start border-end p-3 position-relative border-top border-bottom border-dark-subtle">
    <div id="candidate-identity-list">
        <#if identity_list?size gt 0>
            <@table>
                <tr>
                    <th>#i18n{identityimport.manage_candidateidentities.columnExternalCustomerId}</th>
                    <th>#i18n{identityimport.manage_candidateidentities.columnCustomerId}</th>
                    <th>#i18n{identityimport.manage_batchs.columnStatus}</th>
                    <th>#i18n{identityimport.manage_candidateidentities.columnAPIStatus}</th>
                    <th>#i18n{portal.util.labelActions}</th>
                </tr>
                <@tableHeadBodySeparator />
                <#list identity_list as candidateidentity >
                    <tr>
                        <td>
                            ${candidateidentity.resource.externalCustomerId!}
                        </td>
                        <td>
                            ${candidateidentity.resource.customerId!}
                        </td>
                        <td>
                            <#if candidateidentity.state??><@tag color='warning'>${candidateidentity.state.name}</@tag><#else> not set</#if>
                        </td>
                        <td>
                            ${candidateidentity.resource.status!}
                        </td>
                        <td>
                            <#if candidateidentity.state?? && candidateidentity.state.id == 7 && batch.state?? && batch.state.id == 2 >
                                <@aButton class="manualImportButton" href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?view=importCandidateIdentity&id_identity=${candidateidentity.resource.id}&return_url=jsp/admin/plugins/identityimport/ManageBatchs.jsp?view=manageIdentities&id_state=${current_batch_state.id}&id_batch=${batch.resource.id}&batch_page=${batch_current_page}&application_code=${application_code!}" title='#i18n{identityimport.manage_candidateidentity.labelManualImport}' hideTitle=['all'] buttonIcon='hammer' />
                            </#if>
                            <#if candidateidentity.resource.attributes?? && candidateidentity.resource.attributes?size gt 0>
                                <@customPageColumnBtn idPageColumn="candidateidentity-attribute-list-${candidateidentity.resource.externalCustomerId}" />
                            </#if>
                            <@customPageColumnBtn idPageColumn="candidateidentity-history-${candidateidentity.resource.externalCustomerId}" buttonIcon='th-list'/>
                        </td>
                    </tr>
                </#list>
            </@table>
            <#list identity_list as candidateidentity >
                <#if candidateidentity.resource.attributes?? && candidateidentity.resource.attributes?size gt 0>
                    <@pageColumn width="100%" flush=true responsiveMenuSize="xxl" responsiveMenuPlacement="end" responsiveMenuTitle="#i18n{identityimport.manage_candidateidentityattributes.title} ${candidateidentity.resource.externalCustomerId}" id="candidateidentity-attribute-list-${candidateidentity.resource.externalCustomerId}" class="border-start-0" responsiveMenuClose=true>
                        <@attributeList candidateIdentity=candidateidentity.resource />
                    </@pageColumn>
                    <@pageColumn width="100%" flush=true responsiveMenuSize="xxl" responsiveMenuPlacement="end" responsiveMenuTitle="#i18n{identityimport.manage_candidateidentityattributes.history} ${candidateidentity.resource.externalCustomerId}" id="candidateidentity-history-${candidateidentity.resource.externalCustomerId}" class="border-start-0" responsiveMenuClose=true>
                        ${candidateidentity.history! }
                    </@pageColumn>
                </#if>
            </#list>
        <#else>
            Pas d'identités trouvées
        </#if>
    </div>
    <nav aria-label="Pagination" class="border-top">
        <ul class="pagination justify-content-center mt-3 mb-0">
            <li class="page-item <#if identities_current_page == 1>disabled</#if>">
                <a class="page-link"
                   href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageIdentities&id_state=${current_batch_state.id}&identities_page=${identities_current_page - 1}&batch_page=${batch_current_page}&id_batch=${current_batch_id}&application_code=${application_code!}"
                   tabindex="-1" aria-disabled="true"><i class="ti ti-chevron-left"></i></a>
            </li>
            <#if batch_total_pages gt 6>
                <li class="page-item">
                    <select id="paginationSelect" class="form-select rounded-0" style="width: auto;">
                        <#list 1..identities_total_pages as page>
                            <option value="${page}" <#if identities_current_page == page>selected</#if>>${page}</option>
                        </#list>
                    </select>
                </li>
            <#else>
                <#list 1..batch_total_pages as page>
                    <li class="page-item <#if identities_current_page == page>border-primary-subtle border-end border-top-0 border-start-0 border-bottom-0</#if>">
                        <a class="page-link <#if identities_current_page == page>text-primary-emphasis bg-primary-subtle border border-primary-subtle border-end</#if> <#if identities_current_page == page - 1>border-start-0</#if>"
                           href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageIdentities&id_state=${current_batch_state.id}&identities_page=${identities_current_page}&batch_page=${batch_current_page}&id_batch=${current_batch_id}&application_code=${application_code!}">${page}</a>
                    </li>
                </#list>
            </#if>
            <li class="page-item <#if identities_current_page == identities_total_pages>disabled</#if>">
                <a class="page-link"
                   href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageIdentities&id_state=${current_batch_state.id}&identities_page=${identities_current_page + 1}&batch_page=${batch_current_page}&id_batch=${current_batch_id}&application_code=${application_code!}"><i
                            class="ti ti-chevron-right"></i></a>
            </li>
        </ul>
    </nav>
</div>
<script type="module">
    const selectedItem = document.querySelector('#candidate-identity-list .selected');
    const paginationSelect = document.getElementById("paginationSelect");
    paginationSelect && paginationSelect.addEventListener("change", function () {
        let selectedPage = this.value;
        window.location.href = "jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageBatchs&application_code=${application_code!}&id_state=${current_batch_state.id}&page=" + selectedPage;
    });
    if (selectedItem) {
        setTimeout(function () {
            const ulElement = document.getElementById("candidate-identity-list");
            ulElement.scrollTop = selectedItem.offsetTop - (ulElement.clientHeight / 2) + (selectedItem.clientHeight / 2);
        }, 100);
    }
</script>