<#if errorMsg?has_content>

  <h1 class="errorMessage">${errorMsg}</h1>

<#else>

  <h1>Processed script: "${parameters.scriptName}"</h1>

  <#if data?has_content && (data?is_sequence || data?is_hash)>

    <table class="basic-table" style="border: 1px double; margin:5px">

       <@displayData data=data/>

    </table>

  <#else>

    <h1>No data records retrieved.</h1>

  </#if>

</#if>

<#macro displayData data>

  <#if data?is_sequence>

    <#assign keys = data?first?keys/>

  <#else>

    <#assign keys = data?keys/>

  </#if>

  <#-- Header -->

  <tr>

  <#list keys as key>

    <td class="dark-grid"><b>${key}</b></td>

  </#list>

  </tr>

  <#-- Data -->

  <#if data?is_sequence>

    <#list data as record>

    <tr>

      <#list keys as key>

      <td class="light-grid">${record[key]!""}</td>

      </#list>

    </tr>

    </#list>

  <#else>

    <tr>

      <#list keys as key>

      <td class="light-grid">${data[key]!""}</td>

      </#list>

    </tr>

  </#if>

</#macro>

