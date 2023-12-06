<#--
  Macro: identityMerge

  Renders the UI for merging identities.

  The macro generates a column with actions and UI elements allowing the user to merge identities. 
  This macro makes use of filtered attributes and certain conditions to render buttons and 
  other UI components for each identity attribute.

  @param identity_to_keep The primary identity data object.
  @param identity_to_merge The secondary identity data object to be potentially merged into the primary identity.
  @param current_rule_code The code of the current rule in context.
  @param current_page The current page reference.
  @param service_contract A reference containing attribute definitions and other related data.
  
  @returns A rendered column for merging actions between identities.
  
  Usage:
    <@identityMerge />
-->
<#macro identityMerge>
    <div id="lutece-merge" class="p-0 m-0 position-absolute start-50 translate-middle-x z-2" style="width:100px;">
		<ul class="list-group list-group-flush">
			<#list key_list as current_key >
				<li class="list-group-item text-center d-flex justify-content-center align-items-center border-0" data-name="${current_key}" style="min-height:55px">
					<#assign attributesList = identity_to_merge.attributes?filter(a -> a.key == current_key)>
					<#if attributesList?size gt 0>
						<#list attributesList as attr>
							<#assign attrToKeep = (identity.attributes?filter(a -> a.key == current_key)?first)!{} >
							<#if (!identity.monParisActive && !identity_to_merge.monParisActive && identity.attributes?filter(a -> a.key == current_key)?size == 1 && (attrToKeep?? && attr.certificationLevel > (attrToKeep.certificationLevel)!0) ) || attrToKeep?size == 0 >
								<div class="position-absolute top-50 start-0 end-0 bg-dark border-top border-primary-subtle mediation-line-merge" style="z-index:-1"></div>
								<div class="text-center w-100">
									<@button class='btn btn-rounded border-primary-subtle btn-light m-auto mediation-btn-merge' color='-' buttonIcon='arrow-left' params=' data-key="${attr.key}" data-value="${attr.value}" data-certif="${attr.certifier}" data-certifdate="${attr.certificationDate?date}" data-timestamp-certif="${attr.certificationDate?long}"' />  
								</div>
							</#if>
						</#list>
					
					</#if>
				</li>
			</#list>
		</ul>
	</div>
</#macro>
