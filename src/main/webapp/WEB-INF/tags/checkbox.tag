<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="field" required="true" %>
<spring:bind path="${field}">
    <div class="checkbox ${status.error ? 'has-error' : ''}">
        <label>
            <form:checkbox path="${field}"/><jsp:doBody/>
            <div><form:errors path="${field}" /></div>
        </label>
    </div>
</spring:bind>
