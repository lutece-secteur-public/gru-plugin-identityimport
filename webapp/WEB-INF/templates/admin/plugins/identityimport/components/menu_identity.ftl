<#--
  Element: Menu

  Displays the filtering menu for identity manual import.

  The element produces a column featuring an accordion section that allows the user to filter
  batches based on different states and to provide search criteria to refine the results.

  @param application_code The code of the application to search for.

  @returns A rendered column for identity import and search.

  Usage:
    <@pageColumn ... />
-->
<@pageColumn width="22rem" responsiveMenuSize="xl" responsiveMenuPlacement="start" responsiveMenuTitle="#i18n{identityimport.import_identity.pageTitle}" class="pt-xl-4 px-md-4">
    <@messages infos=infos />
    <@messages errors=errors />
    <@messages warnings=warnings />
    <div class="">
        <h1 class="mb-0 py-2 pb-1">#i18n{identityimport.import_identity.pageTitle}</h1>
        <h3 class="mb-0 py-2 pb-1">${identity.externalCustomerId!''}</h3>
    </div>
    <hr class="d-none d-xxl-block">
    <div class="accordion accordion-flush" id="actionsAccordion">
        <div class="accordion-item bg-transparent">
            <h2 class="accordion-header" id="rulesHeader">
                <button class="accordion-button collapsed px-2 bg-transparent" type="button" data-bs-toggle="collapse" data-bs-target="#rulesContent" aria-expanded="false" aria-controls="rulesContent">
                    <i class="ti ti-filters fs-2 me-2"></i> <strong>#i18n{identityimport.import_identity.actionsLabel}</strong>
                </button>
            </h2>
            <div id="rulesContent" class="accordion-collapse collapse show" aria-labelledby="rulesHeader" data-bs-parent="#actionsAccordion">
                <div class="accordion-body p-0">
                    <ul class="list-group list-group-flush">
                        <#if identity_workflow.actions??>
                            <#list identity_workflow.actions as action >
                                <#if action.id == 9 || action.id == 15>
                                    <div class="list-group-item list-group-item-action border-bottom-0">
                                        <@aButton href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?action=processIdentityAction&actionId=${action.id}&id=${identity_workflow.resource.id}"
                                        title="${action.name}" alt="${action.name}"/>
                                    </div>
                                </#if>
                            </#list>
                        <#else>
                            No action
                        </#if>
                    </ul>
                </div>
            </div>
        </div>
        <div class="accordion-item bg-transparent">
            <h2 class="accordion-header" id="actionsHeader">
                <button class="accordion-button collapsed px-2 bg-transparent" type="button" data-bs-toggle="collapse" data-bs-target="#actionsContent" aria-expanded="false" aria-controls="actionsContent">
                    <i class="ti ti-hammer me-2 fs-2"></i> #i18n{identityimport.import_identity.otherActionsLabel}
                </button>
            </h2>
            <div id="actionsContent" class="accordion-collapse collapse" aria-labelledby="actionsHeader" data-bs-parent="#actionsAccordion">
                <div class="accordion-body">
                    <#if return_url??>
                        <@aButton href="${return_url}" title="#i18n{portal.util.labelCancel}" alt="#i18n{portal.util.labelCancel}"/>
                    </#if>
                </div>
            </div>
        </div>
    </div>
    <hr class="d-none d-xxl-block">
    <div class="alert alert-warning mt-2" role="alert" style="text-align: justify;">
        <p class="m-0 text-body">#i18n{identityimport.select_identities.service.contract.rights.message}</p>
    </div>
</@pageColumn>