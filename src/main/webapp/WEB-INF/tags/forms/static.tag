<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="value" required="true" %>
<div class="form-group">
    <label class="control-label"><spring:message code="${field}"/></label>
    <p class="form-control-static">${value}</p>
</div>
