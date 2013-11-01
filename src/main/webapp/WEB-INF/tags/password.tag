<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="label" required="true" %>
<spring:bind path="${field}">
    <div class="form-group ${status.error ? 'has-error' : ''}">
        <label class="control-label" for="${field}">${label}</label>
        <form:password cssClass="form-control input-sm" id="${field}" path="${field}"/>
        <form:errors path="${field}" />
    </div>
</spring:bind>
