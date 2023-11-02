<#--
  Element: Menu

  Displays the filtering menu for import batches.

  The element produces a column featuring an accordion section that allows the user to filter 
  batches based on different states and to provide search criteria to refine the results.

  @param application_code The code of the application to search for.
  
  @returns A rendered column for identity import and search.
  
  Usage:
    <@pageColumn ... />
-->
<@pageColumn id="mediation-filter-menu" width="22rem" responsiveMenuSize="xl" responsiveMenuPlacement="start"
    responsiveMenuTitle="#i18n{identityimport.manage_batchs.title}" class=" pt-xl-4 px-md-4 ">
    <div class="">
        <h1 class="mb-0 py-2 pb-1">#i18n{identityimport.manage_batchs.title}</h1>
    </div>
    <hr class="d-none d-xxl-block">
    <div class="accordion accordion-flush" id="searchAccordion">
        <div class="accordion-item bg-transparent">
            <h2 class="accordion-header" id="rulesHeader">
                <button class="accordion-button collapsed px-2 bg-transparent" type="button" data-bs-toggle="collapse" data-bs-target="#rulesContent" aria-expanded="false" aria-controls="rulesContent">
                    <i class="ti ti-filters fs-2 me-2"></i> <strong>#i18n{identityimport.manage_batchs.columnStatus}</strong>
                </button>
            </h2>
            <div id="rulesContent" class="accordion-collapse collapse show" aria-labelledby="rulesHeader" data-bs-parent="#searchAccordion">
                <div class="accordion-body p-0">
                    <ul class="list-group list-group-flush">
                        <#list batch_state_list as state>
                            <#assign isCurrent = (current_batch_state?? && state.id == current_batch_state.id) />
                            <a href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageBatchs&id_state=${state.id}&application_code=${application_code!}"
                                class="list-group-item list-group-item-action border-bottom-0 <#if isCurrent>text-primary</#if>"
                                id="${state.id}">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div>
                                        <span class="my-auto fw-bolder">${state.name}</span><br/>
                                        <span class="my-auto">${state.description}</span>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <#if state.resourceCount?number gt 0>
                                            <@tag color="danger" class="border-1">${state.resourceCount}</@tag>
                                        <#else>
                                            <@tag color="success">${state.resourceCount}</@tag>
                                        </#if>
                                    </div>
                                </div>
                            </a>
                        </#list>
                    </ul>
                </div>
            </div>
        </div>
        <div class="accordion-item bg-transparent">
            <h2 class="accordion-header" id="searchHeader">
                <button class="accordion-button collapsed px-2 bg-transparent" type="button" data-bs-toggle="collapse" data-bs-target="#searchContent" aria-expanded="false" aria-controls="searchContent">
                    <i class="ti ti-user-search me-2 fs-2"></i> #i18n{identityimport.manage_batchs.searchTitle}
                </button>
            </h2>
            <div id="searchContent" class="accordion-collapse collapse" aria-labelledby="searchHeader" data-bs-parent="#searchAccordion">
                <div class="accordion-body">
                    <@tform method='post' name='manage_batch' action='jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_manageBatch' id='manageBatch'>
                        <input type='hidden' name='id_state' value='<#if current_batch_state??>${current_batch_state.id!""}<#else>0</#if>' />
                        <@formGroup labelKey='#i18n{identityimport.manage_batchs.columnAppCode}' labelFor='application_code' hideLabel=['all'] rows=2>
                            <@input type='text' id='application_code' name='application_code' value='${application_code!""}' placeHolder='#i18n{identityimport.manage_batchs.columnAppCode}' size='' />
                        </@formGroup>
                        <@formGroup rows=2>
                            <@button type='submit' style='w-100' buttonIcon='search' title='#i18n{portal.util.labelSearch}' color='primary' size='' />
                        </@formGroup>
                    </@tform>
                </div>
            </div>
        </div>
    </div>
</@pageColumn>