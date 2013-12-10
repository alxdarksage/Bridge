<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="field" required="true" %>
<%@ attribute name="hasLabel" required="false" %>
<spring:bind path="${field}">
    <div class="form-group ${status.error ? 'has-error' : ''}">
        <c:if test="${empty hasLabel or hasLabel == 'true'}">
            <label class="control-label" for="${field}"><spring:message code="${field}"/></label>
        </c:if>
        <form:textarea cssClass="form-control input-sm" id="${field}" path="${field}"/>
        <form:errors id="${field}_errors" path="${field}" htmlEscape="false"/>
        <jsp:doBody/>
    </div>
</spring:bind>
