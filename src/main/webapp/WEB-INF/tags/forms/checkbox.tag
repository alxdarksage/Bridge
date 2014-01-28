<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="required" required="false" %>
<spring:bind path="${field}">
    <div class="checkbox ${status.error ? 'has-error' : ''}">
        <label>
            <form:checkbox id="${field}" path="${field}"/><jsp:doBody/>
            <c:if test="${required}"><span class="error-text">*</span></c:if>
            <br/><form:errors id="${field}_errors" path="${field}" htmlEscape="false"/>
        </label>
    </div>
</spring:bind>
