<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<div class="header row">
    <div class="col-sm-6 community-header">
        <div>
        <a href="#" id="open-left" class="visible-xs"> <!--  class="visible-xs"  -->
            <span class="glyphicon glyphicon-align-justify"></span>
        </a>
        <c:choose>
            <c:when test="${not empty community}">
                <c:url var="headerUrl" value="/communities/${community.id}.html"/>    
            </c:when>
            <c:otherwise>
                <c:url var="headerUrl" value="/portal/index.html"/>
            </c:otherwise>
        </c:choose>
        <a href="${headerUrl}" class="visible-xs visible-sm visible-md visible-lg">
            <spring:message code="${code}"/>
        </a>
        </div>
    </div>
    <div class="col-sm-6 portal-header">
        <div class="portal-subheader visible-sm visible-md visible-lg">
            <a href="<c:url value='/portal/index.html'/>">Bridge Community Portal</a>
        </div>
    </div>
</div>
