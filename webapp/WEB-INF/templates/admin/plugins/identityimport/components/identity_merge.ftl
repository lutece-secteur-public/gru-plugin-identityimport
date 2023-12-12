<#--
  Macro: identityMerge

  Renders the UI for merging identities.

  The macro generates a column with actions and UI elements allowing the user to merge identities. 
  This macro makes use of filtered attributes and certain conditions to render buttons and 
  other UI components for each identity attribute.

  @param identityToKeep The primary identity data object.
  @param identityToMerge The secondary identity data object to be potentially merged into the primary identityToKeep.
  
  @returns A rendered column for merging actions between identities.
  
  Usage:
    <@identityMerge />
-->
<#macro identityMerge identityToMerge identityToKeep keyList>
    <div id="lutece-merge" class="p-0 m-0 position-absolute start-50 translate-middle-x z-2" style="width:100px;">
        <ul class="list-group list-group-flush">
            <li class="position-relative d-flex justify-content-center align-items-center" style="height:155px;">
                <div class="position-absolute top-50 start-0 end-0" style="z-index:-1"></div>
                <div class="text-center w-100">

                </div>
            </li>
            <#list keyList as current_key >
                <li class="list-group-item text-center d-flex justify-content-center align-items-center border-0" data-name="${current_key}" style="min-height:55px">
                    <#list identityToMerge.attributes?filter(a -> a.key == current_key) as attrToMerge>
                        <#assign attrToKeep = (identityToKeep.attributes?filter(a -> a.key == current_key)?first)!{} >
                        <#if ( !identityToKeep.monParisActive && !identityToMerge.monParisActive && ( attrToMerge.certificationLevel gte (attrToKeep.certificationLevel)!0 ) && ( !(attrToKeep?has_content) || (attrToKeep.value != attrToMerge.value) ) )
                             || !(attrToKeep?has_content) >
                            <div class="position-absolute top-50 start-0 end-0 bg-dark border-top border-primary-subtle mediation-line-merge" style="z-index:-1"></div>
                            <div class="text-center w-100">
                                <@button class='btn btn-rounded border-primary-subtle btn-light m-auto mediation-btn-merge' color='-' buttonIcon='arrow-left' params=' data-key="${attrToMerge.key}" data-value="${attrToMerge.value}" data-certif="${attrToMerge.certifier}" data-certifdate="${attrToMerge.certificationDate?date}" data-timestamp-certif="${attrToMerge.certificationDate?long}"' />  
                            </div>
                        </#if>
                    </#list>
                </li>
            </#list>
        </ul>
    </div>
</#macro>
