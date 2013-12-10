<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="field" required="true" %>
<spring:bind path="${field}">
    <div class="checkbox ${status.error ? 'has-error' : ''}">
        <label>
            <form:checkbox id="${field}" path="${field}"/><jsp:doBody/>
            <br/><form:errors id="${field}_errors" path="${field}" htmlEscape="false"/>
        </label>
    </div>
</spring:bind>
