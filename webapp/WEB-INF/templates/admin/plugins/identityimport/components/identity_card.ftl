<#--
  Macro: identityCard
  Renders a card UI for identity details.
  @param identity The main data object containing the identity's details.
  @param index The index of the current identity in the list.
  @param merge Optional boolean parameter to conditionally render merge-related UI elements.
  @param class Optional CSS classes for customization.
  @param width Optional width for the card.
  @returns A rendered identity card based on provided parameters.
-->
<#macro identityCard identity identity_workflow firstIdentity index serviceContract merge=false candidate=false class="" width="">
    <#assign familyNameAttr = identity.attributes?filter(a -> a.key == "family_name")?first!{}>
    <#assign firstNameAttr = identity.attributes?filter(a -> a.key == "first_name")?first!{}>
    <#assign emailAttr = identity.attributes?filter(a -> a.key == "email")?first!{}>
    <#assign birthdateAttr = identity.attributes?filter(a -> a.key == "birthdate")?first!{}>
    <div class="lutece-compare-item-container border-end p-3 position-relative <#if index == 0>bg-primary-subtle border border-primary-subtle rounded-start-5<#else> border-top border-bottom</#if><#if !merge && index == 0> border-2 border-end-dashed </#if><#if !merge && index != 0> border-dark-subtle<#elseif merge && index != 0> border-warning-subtle</#if> ${class}" style="<#if width!=''>width:${width}</#if>">
        <div class="lutece-compare-item card p-0 rounded-5 shadow-xl mb-0">
            <div class="py-4 text-center">
                <h3 class="px-2 text-truncate">
                    <#if familyNameAttr??>
                        <#if familyNameAttr.value?has_content>
                            ${familyNameAttr.value}
                        <#else>
                            <span class="text-warning">Vide</span>
                        </#if>
                    <#else>
                        <span class="text-warning">Inexistant</span>
                    </#if>
                    <#if firstNameAttr??>
                        <#if firstNameAttr.value?has_content>
                            ${firstNameAttr.value}
                        <#else>
                            <span class="text-warning">Vide</span>
                        </#if>
                    <#else>
                        <span class="text-warning">Inexistant</span>
                    </#if>
                </h3>
                <div class="d-flex flex-row justify-content-center align-items-center mt-2">
                    <div class="mr-2">
                        <#if identity.quality.quality?is_number>
                            <#assign qualityPercent=(identity.quality.quality * 100)?round>
                            <#if qualityPercent gt 79>
                                <@tag color="success" >#i18n{identityimport.quality} : ${qualityPercent}%</@tag>
                            <#elseif qualityPercent gt 50 && qualityPercent lt 80>
                                <@tag color="warning">#i18n{identityimport.quality} : ${qualityPercent}%</@tag>
                            <#else>
                                <@tag color="danger">#i18n{identityimport.quality} : ${qualityPercent}%</@tag>
                            </#if>
                        <#else>
                            <@tag color="danger">-</@tag>
                        </#if>
                    </div>
                    <div>
                        <#if !candidate>
                            <#if identity.monParisActive>
                                <@tag color="success" class="ms-2">MON PARIS</@tag>
                            <#else>
                                <@tag color="danger" class="ms-2 text-decoration-line-through">MON PARIS</@tag>
                            </#if>
                        </#if>
                    </div>
                    <div>
                        <#if !candidate>
                            <#if identity.customerId??>
                                <@tag color="info" class="ms-2" params="data-toggle='tooltip' data-placement='top' title='${identity.customerId}'">CUID</@tag>
                            </#if>
                        </#if>
                    </div>
                </div>
                <div class="mt-3">
                    <#if candidate>
                        <button type="button" class="btn btn-warning" data-name="identity-cuid-${index}" data-cuid="${identity.customerId!''}">
                            #i18n{identityimport.import_identity.importedIdentity}
                        </button>
                    <#else>
                        <#if merge>
                            <button type="button" class="btn btn-success" data-name="identity-cuid-${index}" data-cuid="${identity.customerId!''}">
                                #i18n{identityimport.import_identity.selectedIdentity}
                            </button>
                        <#else>
                            <button type="button" class="btn btn-success" data-name="identity-cuid-${index}" data-cuid="${identity.customerId!''}">
                                ${identity.duplicateDefinition.duplicateSuspicion.duplicateRuleCode!''}
                            </button>
                        </#if>
                    </#if>
                </div>
            </div>
            <ul class="list-group list-group-flush rounded-bottom-5">
                <#list key_list as current_key >
                    <#assign attributeDefinition = (serviceContract.attributeDefinitions?filter(a -> a.keyName == current_key)?first)!{} />
                    <li class="list-group-item d-flex justify-content-center align-items-center p-0 border-start-0 border-end-0" data-name="${current_key}" data-key="${current_key}" style="min-height:55px">
                        <div class="w-100 d-flex">
                            <#assign attributesList=identity.attributes?filter(a -> a.key == current_key)>
                            <#if (!merge && index != 0) || (merge && index == 0)>
                                <#assign firstIdentityAttr=firstIdentity.attributes?filter(a -> a.key == current_key)?first!{}>
                            </#if>
                            <#if attributesList?size gt 0>
                                <#list attributesList as attr>
                                    <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                                        <div class="opacity-50">
                                            ${current_key} <#if !attributeDefinition.attributeRight.writable> <i class="ti ti-x" style="color: red"></i> </#if>
                                        </div>
                                        <div class="fw-bold attribute-container">
                                            <h3 class="attribute-value mb-0 fw-bold <#if !attr.value?has_content>text-warning</#if> <#if ((!merge && index != 0) || (merge && index == 0)) && ( !(firstIdentityAttr.value?has_content) || firstIdentityAttr.value != attr.value )>text-danger</#if>">
                                                <#if attr.value?? && attr.value?has_content>
                                                    ${attr.value}
                                                <#else>
                                                    Vide
                                                </#if>
                                            </h3>
                                        </div>
                                    </div>
                                    <div class="flex-1 border-start py-2 px-2 text-break" style="width:110px;min-width:110px;max-width:110px;">
                                        <#if attr.certificationDate??>
                                            <div class="text-center opacity-50">
                                                ${attr.certificationDate?date}
                                            </div>
                                        </#if>
                                        <#if attr.certifier??>
                                            <div class="certifier text-truncate text-center w-100">
                                                <span class="fw-medium">
                                                    ${attr.certifier}
                                                </span>
                                            </div>
                                        </#if>
                                    </div>
                                </#list>
                            <#else>
                                <div class="flex-1 flex-grow-1 py-2 px-3 text-break">
                                    <div class="opacity-50">
                                        ${current_key} <#if !attributeDefinition.attributeRight.writable> <i class="ti ti-x" style="color: red"></i> </#if>
                                    </div>
                                    <div class="fw-bold attribute-container">
                                        <h3 class="attribute-value mb-0 fw-bold text-warning">
                                            Inexistant
                                        </h3>
                                    </div>
                                </div>
                                <div class="flex-1 border-start py-2 px-2 text-break" style="width:110px;min-width:110px;max-width:110px;"></div>
                            </#if>
                        </div>
                    </li>
                </#list>
            </ul>
            <div class="py-4 text-center">
                <#if !merge>
                    <#if index != 0>
                        <@aButton href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?view_completeIdentity=&id_identity=${identity_workflow.resource.id}&selected_customer_id=${identity.customerId!''}" title="#i18n{identityimport.select_identities.buttonMergeDuplicate}" alt="#i18n{identityimport.select_identities.buttonMergeDuplicate}"/>
                    <#else>
                        <#list identity_workflow.actions as action >
                            <#if action.id == 4 >
                                <@aButton href="jsp/admin/plugins/identityimport/ManageBatchs.jsp?action=processIdentityAction&actionId=${action.id}&id=${identity_workflow.resource.id}" title="${action.name}" alt="${action.name}"/>
                            </#if>
                        </#list>
                    </#if>
                </#if>
            </div>
        </div>
    </div>
</#macro>
