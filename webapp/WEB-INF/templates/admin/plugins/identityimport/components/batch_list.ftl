<#if current_batch_state?has_content && batch_state_list?has_content && batch_list?has_content && batch_current_page??>
    <#list batch_state_list as state>
        <#if state.id == current_batch_state.id>
            <#assign title><strong>${state.resourceCount}</strong> batch(es) ${state.name}</#assign>
        </#if>
    </#list>
    <#assign isClosed = false />
<#--  <#if batch_list?? && batch_list?size gt 3>-->
<#--    <#assign isClosed = true />-->
<#--  </#if>-->
    <@pageColumn width="25rem" flush=true responsiveMenuSize="xxl" responsiveMenuPlacement="end"
    responsiveMenuTitle="#i18n{identityimport.manage_batchs.title}" id="mediation-duplicate-list" class=" border-start-0" responsiveMenuClose=isClosed>
        <div class="border-bottom p-4 sticky-top">
            <h1 class="text-center mb-0 py-2 pb-1">${title!''}
            </h1>
        </div>
        <ul id="duplicate-list" class="list-group list-group-flush overflow-auto" style="height:calc(100vh - 280px)">
            <#if batch_list?size gt 0>
                <#list batch_list as batch>
                    <#assign selectedClasses></#assign>
                <#--          <#if suspicious_identity?? && suspicious_identity.customerId == batch.suspiciousIdentity.customerId >-->
                <#--            <#assign selectedClasses>bg-secondary-subtle shadow selected</#assign>-->
                <#--          </#if>-->
                    <#assign isCurrent = (current_batch_id?? && batch.resource.id == current_batch_id) />
                    <a href='jsp/admin/plugins/identityimport/ManageBatchs.jsp?view=manageIdentities&id_state=${current_batch_state.id}&id_batch=${batch.resource.id}&batch_page=${batch_current_page}&application_code=${application_code!}'
                       class="list-group-item list-group-item-action px-4 py-3 ${selectedClasses} <#if isCurrent>text-primary</#if>">
                        <#assign reference = batch.resource.reference!{}>
                        <#assign date = batch.resource.date!{}>
                        <#assign app = batch.resource.appCode!{}>
                        <#assign comment = batch.resource.comment!{}>
                        <div class="d-flex w-100 justify-content-between">
                            <h3 class="mb-1 title mt-1 text-break fw-bold">
                                ${reference!'-'}
                            </h3>
                            <div>
                                <@tag color="primary"><strong>${batch.nbSubResource}</strong> #i18n{identityimport.manage_batchs.identitiesLabel}</@tag>
                            </div>
                        </div>
                        <div>${date!''} - ${app!''}</div>
                        <div>${comment!''}</div>
                    </a>
                </#list>
            </#if>
        </ul>
        <nav aria-label="Pagination" class="border-top">
            <ul class="pagination justify-content-center mt-3 mb-0">
                <li class="page-item <#if batch_current_page == 1>disabled</#if>">
                    <a class="page-link"
                       href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageBatchs&id_state=${current_batch_state.id}&batch_page=${batch_current_page - 1}&application_code=${application_code!}"
                       tabindex="-1" aria-disabled="true"><i class="ti ti-chevron-left"></i></a>
                </li>
                <#if batch_total_pages gt 6>
                    <li class="page-item">
                        <select id="paginationSelect" class="form-select rounded-0" style="width: auto;">
                            <#list 1..batch_total_pages as page>
                                <option value="${page}" <#if batch_current_page == page>selected</#if>>${page}</option>
                            </#list>
                        </select>
                    </li>
                <#else>
                    <#list 1..batch_total_pages as page>
                        <li class="page-item <#if batch_current_page == page>border-primary-subtle border-end border-top-0 border-start-0 border-bottom-0</#if>">
                            <a class="page-link <#if batch_current_page == page>text-primary-emphasis bg-primary-subtle border border-primary-subtle border-end</#if> <#if batch_current_page == page - 1>border-start-0</#if>"
                               href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageBatchs&id_state=${current_batch_state.id}&batch_page=${page}&application_code=${application_code!}">${page}</a>
                        </li>
                    </#list>
                </#if>
                <li class="page-item <#if batch_current_page == batch_total_pages>disabled</#if>">
                    <a class="page-link"
                       href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageBatchs&id_state=${current_batch_state.id}&batch_page=${batch_current_page + 1}&application_code=${application_code!}"><i
                                class="ti ti-chevron-right"></i></a>
                </li>
            </ul>
        </nav>
    </@pageColumn>
    <script type="module">
        const selectedItem = document.querySelector('#duplicate-list .selected');
        const paginationSelect = document.getElementById("paginationSelect");
        paginationSelect && paginationSelect.addEventListener("change", function () {
            let selectedPage = this.value;
            window.location.href = "jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageBatchs&application_code=${application_code!}&id_state=${current_batch_state.id}&page=" + selectedPage;
        });
        if (selectedItem) {
            setTimeout(function () {
                const ulElement = document.getElementById("duplicate-list");
                ulElement.scrollTop = selectedItem.offsetTop - (ulElement.clientHeight / 2) + (selectedItem.clientHeight / 2);
            }, 100);
        }
    </script>
</#if>