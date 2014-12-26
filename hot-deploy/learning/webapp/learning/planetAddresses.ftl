<b>Planet: <i>${planet.planetName}</i></b>

<br/>

<table border="1">

      <tr>

      <td>contactMechId</td>

      <td>Address</td>

      <td>City</td>

      <td>Postal Code</td>

      </tr>

      <#list planetAddresses as address>

      <tr>

        <td>${address.contactMechId}</td>

        <td>${address.address1!""} ${address.address2!""}</td>

        <td>${address.city!""}</td>

        <td>${address.postalCode!""}</td>

      </tr>

      </#list>

</table>

