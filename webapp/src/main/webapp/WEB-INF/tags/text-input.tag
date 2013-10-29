<%@ attribute name="field" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:bind path="${field}">
	<div class="form-group ${status.error ? 'has-error' : ''}">
		<label class="control-label" for="${field}">${label}</label>
		<form:input cssClass="form-control" id="${field}" path="${field}"/>
		<form:errors path="${field}" />
	</div>
</spring:bind>
