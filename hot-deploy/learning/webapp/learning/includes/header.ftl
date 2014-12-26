<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 

    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>

    <head>

<#if layoutSettings.javaScripts?has_content>

  <#list layoutSettings.javaScripts as javaScript>

         <script language="javascript" src="<@ofbizContentUrl>${javaScript}</@ofbizContentUrl>" type="text/javascript"></script>

  </#list>

</#if>

<#if layoutSettings.styleSheets?has_content>

  <#list layoutSettings.styleSheets as styleSheet>

    <link rel="stylesheet" href="<@ofbizContentUrl>${styleSheet}</@ofbizContentUrl>" type="text/css"/>

  </#list>

<#else>

  <link rel="stylesheet" href="<@ofbizContentUrl>/flatgrey/maincss.css</@ofbizContentUrl>" type="text/css"/>

</#if>


         <title>${sections.render("title")}</title>

    </head>

    <body>



