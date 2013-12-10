<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="field" required="true" %>
<spring:bind path="${field}">
	<div class="form-group ${status.error ? 'has-error' : ''}">
		<label class="control-label" for="${field}"><spring:message code="${field}"/></label>
		<form:input cssClass="form-control input-sm" id="${field}" path="${field}"/>
        <form:errors id="${field}_errors" path="${field}" htmlEscape="false"/>
        <jsp:doBody/>
	</div>
</spring:bind>
