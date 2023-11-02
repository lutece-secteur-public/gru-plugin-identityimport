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
                            <@aButton href='jsp/admin/plugins/identityimport/ManageCandidateIdentities.jsp?view=importCandidateIdentity&id=${candidateidentity.resource.id}' title='#i18n{portal.util.labelUpload}' hideTitle=['all'] buttonIcon='upload' />

                            <@aButton href='jsp/admin/plugins/identityimport/ManageCandidateIdentityAttributes.jsp?id_identity=${candidateidentity.resource.id}&id_batch=${current_batch_id}' title='#i18n{identityimport.manage_candidateidentityattributes.title}' hideTitle=['all'] buttonIcon='list' />

<#--                            <@aButton href='jsp/admin/plugins/identityimport/ManageCandidateIdentities.jsp?view=modifyCandidateIdentity&id=${candidateidentity.resource.id}&id_batch=${current_batch_id}' title='#i18n{portal.util.labelModify}' hideTitle=['all'] buttonIcon='pencil' />-->

<#--                            <@aButton href='jsp/admin/plugins/identityimport/ManageCandidateIdentities.jsp?action=confirmRemoveCandidateIdentity&id=${candidateidentity.resource.id}&id_batch=${current_batch_id}' title='#i18n{portal.util.labelDelete}' buttonIcon='trash' hideTitle=['all'] color='btn-danger'  />-->

                        </td>
                    </tr>
                </#list>
            </@table>
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