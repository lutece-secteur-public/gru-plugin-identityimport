<#macro attributeList candidateIdentity  class="" width="">
    <@table>
        <tr>
            <th>#i18n{identityimport.manage_candidateidentityattributes.columnKey}</th>
            <th>#i18n{identityimport.manage_candidateidentityattributes.columnValue}</th>
            <th>#i18n{identityimport.manage_candidateidentityattributes.columnCertProcess}</th>
            <th>#i18n{identityimport.manage_candidateidentityattributes.columnCertDate}</th>
        </tr>
        <@tableHeadBodySeparator />
        <#list candidateIdentity.attributes as candidateidentityattribute >
            <tr>
                <td>
                    ${candidateidentityattribute.code!}
                </td>
                <td>
                    <#if candidateidentityattribute.code == 'gender'>
                        <#if candidateidentityattribute.value == '0'>
                            #i18n{identityimport.select_identities.undefined}
                        <#elseif candidateidentityattribute.value == '1'>
                            #i18n{identityimport.select_identities.female}
                        <#elseif candidateidentityattribute.value == '2'>
                            #i18n{identityimport.select_identities.male}
                        <#else>
                            ${candidateidentityattribute.value!}
                        </#if>
                    <#else>
                        ${candidateidentityattribute.value!}
                    </#if>
                </td>
                <td>
                    ${candidateidentityattribute.certProcess!}
                </td>
                <td>
                    ${candidateidentityattribute.certDate!}
                </td>
            </tr>
        </#list>
    </@table>
</#macro>