<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="field" required="true" %>
<spring:bind path="${field}">
    <div class="checkbox ${status.error ? 'has-error' : ''}">
        <label>
            <form:checkbox id="${field}" path="${field}"/><jsp:doBody/>
            <br/><form:errors id="${field}_errors" path="${field}" htmlEscape="false"/>
        </label>
    </div>
</spring:bind>
