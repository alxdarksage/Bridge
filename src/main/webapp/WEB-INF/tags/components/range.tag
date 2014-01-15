<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>
<div class="multi">
    <sage:router element="${element.children[0]}"/>&mdash;<sage:router element="${element.children[1]}"/>
</div> 
