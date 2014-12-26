<#assign selected = headerItem?default("void")>

<div id="app-navigation">

  <h2>${uiLabelMap.LearningApplication}</h2>

  <ul>

    <li<#if selected == "main"> class="selected"</#if>> 

        <a href="<@ofbizUrl>main</@ofbizUrl>">${uiLabelMap.CommonMain}</a></li>

    <#if userLogin?has_content>

      <li class="opposed"><a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a></li>

    <#else>

      <li class="opposed"><a href="<@ofbizUrl>${checkLoginUrl?if_exists}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a></li>

    </#if>

  </ul>

  <br class="clear"/>

</div>

