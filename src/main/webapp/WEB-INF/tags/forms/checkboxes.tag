<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="field" required="true" type="java.lang.String"  %>
<%@ attribute name="items" required="true" type="java.util.List" %>
<%@ attribute name="label" required="true" type="java.lang.String" %>
<div class="form-group">
    <label><spring:message code="${label}"/></label>
    <div class="checkbox-box">
        <c:forEach items="${items}" var="item">
            <div class="checkbox" title="${item.label} Membership">
                <label>
                    <c:choose>
                        <c:when test="${item.selected}">
                            <input type="checkbox" name="${field}" value="${item.id}" checked="checked" />
                        </c:when>
                        <c:otherwise>
                            <input type="checkbox" name="${field}" value="${item.id}" />
                        </c:otherwise>
                    </c:choose>
                    ${item.label}
                </label>
            </div>
        </c:forEach>
    </div>
    <spring:bind path="${field}">
        <div class="${status.error ? 'has-error' : ''}">
            <form:errors id="${field}_errors" path="${field}" htmlEscape="false"/>
        </div>
    </spring:bind>
</div>