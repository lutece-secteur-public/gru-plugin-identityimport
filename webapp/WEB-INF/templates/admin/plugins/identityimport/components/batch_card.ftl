<#--
  Macro: batchCard
  Renders a card UI for batch details.
  @param batch The main data object containing the batch details.
  @param class Optional CSS classes for customization.
  @param width Optional width for the card.
  @returns A rendered batch card based on provided parameters.
-->
<#macro batchCard batch class="" width="">
    <div class="lutece-compare-item-container border-start border-end p-3 position-relative border-top border-bottom border-dark-subtle ${class}" style="<#if width!=''>width:${width}</#if>">
        <div class="row mx-0">
            <div class="col-6 pl-0">
                <div class="lutece-compare-item card p-0 rounded-5 shadow-xl my-3">
                    <div class="py-4 text-center">
                        <h3 class="px-2 text-truncate">
                            Batch
                        </h3>
                    </div>
                    <ul class="list-group list-group-flush rounded-bottom-5">
                        <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                            <div class="w-100 d-flex">
                                <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                                    <div class="opacity-50">
                                        #i18n{identityimport.modify_batch.labelReference}
                                    </div>
                                    <div class="fw-bold">
                                        <h3 class="mb-0 fw-bold">
                                            ${batch.resource.reference}
                                        </h3>
                                    </div>
                                </div>
                            </div>
                        </li>
                        <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                            <div class="w-100 d-flex">
                                <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                                    <div class="opacity-50">
                                        #i18n{identityimport.modify_batch.labelDate}
                                    </div>
                                    <div class="fw-bold">
                                        <h3 class="mb-0 fw-bold">
                                            ${batch.resource.date}
                                        </h3>
                                    </div>
                                </div>
                            </div>
                        </li>
                        <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                            <div class="w-100 d-flex">
                                <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                                    <div class="opacity-50">
                                        #i18n{identityimport.modify_batch.labelUser}
                                    </div>
                                    <div class="fw-bold">
                                        <h3 class="mb-0 fw-bold">
                                            ${batch.resource.user}
                                        </h3>
                                    </div>
                                </div>
                            </div>
                        </li>
                        <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                            <div class="w-100 d-flex">
                                <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                                    <div class="opacity-50">
                                        #i18n{identityimport.modify_batch.labelAppCode}
                                    </div>
                                    <div class="fw-bold">
                                        <h3 class="mb-0 fw-bold">
                                            ${batch.resource.appCode}
                                        </h3>
                                    </div>
                                </div>
                            </div>
                        </li>
                        <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                            <div class="w-100 d-flex">
                                <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                                    <div class="opacity-50">
                                        #i18n{identityimport.modify_batch.labelComment}
                                    </div>
                                    <div class="fw-bold">
                                        <h3 class="mb-0 fw-bold">
                                            ${batch.resource.comment}
                                        </h3>
                                    </div>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="col-6 pr-0">
                <div class="lutece-compare-item card p-0 rounded-5 shadow-xl my-3">
                    <div class="py-4 text-center">
                        <h3 class="px-2 text-truncate">
                            Statistiques
                        </h3>
                    </div>
                    <ul class="list-group list-group-flush rounded-bottom-5">
                        <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                            <div class="w-100 d-flex">
                                <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                                    <div class="opacity-50">
                                        Nombre total d'identités
                                    </div>
                                    <div class="fw-bold">
                                        <h3 class="mb-0 fw-bold">
                                            ${batch.nbSubResource}
                                        </h3>
                                    </div>
                                </div>
                            </div>
                        </li>
                        <#assign totalCount = batch.nbSubResource />
                        <#if batch.subResourceStates??>
                            <#list batch.subResourceStates as subResourceState>
                                <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                                    <div class="w-100 d-flex">
                                        <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                                            <div class="opacity-50">
                                                ${subResourceState.name} <@tag color="primary"><strong>${(subResourceState.resourceCount * 100 / totalCount)?string("0.##")}</strong>%</@tag>
                                            </div>
                                            <div class="fw-bold">
                                                <h3 class="mb-0 fw-bold">
                                                    ${subResourceState.resourceCount}
                                                </h3>
                                            </div>
                                        </div>
                                    </div>
                                </li>
                            </#list>
                        </#if>
                    </ul>
                </div>
            </div>
        </div>
        <div class="lutece-compare-item card p-0 rounded-5 shadow-xl my-3">
            <div class="py-4 text-center">
                <h3 class="px-2 text-truncate">
                    Workflow
                </h3>
            </div>
            <ul class="list-group list-group-flush rounded-bottom-5">
                <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                    <div class="w-100 d-flex">
                        <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                            <div class="opacity-50">
                                Statut
                            </div>
                            <div class="fw-bold">
                                <h3 class="mb-0 fw-bold">
                                    <#if batch.state??><@tag  color='warning'>${batch.state.name}</@tag><#else> not set</#if>
                                </h3>
                            </div>
                        </div>
                    </div>
                </li>
                <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                    <div class="w-100 d-flex">
                        <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                            <div class="opacity-50">
                                Actions
                            </div>
                            <div class="fw-bold">
                                <h3 class="mb-0 fw-bold">
                                    <#if batch.actions??>
                                        <#list batch.actions as action >
                                            <@aButton href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?action=processAction&id_action=${action.id}&id_batch=${batch.resource.id}&application_code=${application_code!}"
                                            title="${action.name}" alt="${action.name}"  size='md' />
                                            </a>
                                        </#list>
                                    <#else>no action
                                    </#if>
                                </h3>
                            </div>
                        </div>
                    </div>
                </li>
                <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" style="min-height:55px">
                    <div class="w-100 d-flex">
                        <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                            <div class="opacity-50">
                                Historique
                            </div>
                            <div class="fw-bold">
                                <h3 class="mb-0 fw-bold">
                                    ${batch.history! }
                                </h3>
                            </div>
                        </div>
                    </div>
                </li>
            </ul>
        </div>
    </div>
    <style>
        .pl-0 {
            padding-left: 0!important;
        }
        .pr-0 {
            padding-right: 0!important;
        }
    </style>
</#macro>
