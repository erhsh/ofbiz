<tr>

<td class="label">Planet</td>

<td>

  <#assign fieldName = "planet"/>

  <select name="${fieldName}">

    <#if (planet)??>

      <option value="${planet.planetId}"/>${(planet.planetName)!""}</option>

    </#if>

    <option></option>

    <#if request.getParameter("${fieldName}")??>

      <#assign requestPlanet = request.getParameter("${fieldName}")/>

    </#if>

    <#list planets as planet>

      <option value="${planet.planetId}" <#if requestPlanet?? && requestPlanet == planet.planetId>selected</#if>>

        ${planet.planetName}

      </option>

    </#list>

  </select>

</td>

</tr>

