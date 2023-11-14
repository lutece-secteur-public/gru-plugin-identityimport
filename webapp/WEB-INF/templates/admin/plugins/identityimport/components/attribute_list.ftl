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
                    ${candidateidentityattribute.value!}
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