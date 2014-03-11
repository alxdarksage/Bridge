<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="content" required="true" fragment="true" %>
<%@ attribute name="scripts" required="false" fragment="true" %>
<sage:main code="${code}" mobile="true">
    <jsp:attribute name="navigation">
        <div class="panel panel-default">
            <div class="panel-heading"><b><spring:message code="Trackers"/></b></div>
            <div class="panel-body">
                <ul class="list-group">
                    <c:forEach var="tracker" items="${trackers}">
	                    <li class="list-group-item">
	                        <a href="<c:url value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${tracker.id}.html"/>">
	                           ${(not empty tracker.name) ? tracker.name : tracker.description}
                           </a>
	                    </li>
                    </c:forEach>
                </ul>                   
            </div>
        </div>    
    </jsp:attribute>
    <jsp:attribute name="content"><jsp:invoke fragment="content" /></jsp:attribute>
    <jsp:attribute name="scripts">
        <script src="<c:url value='/assets/trackers.js'/>"></script>
        <script src="<c:url value='/static/webshims-stable/js-webshim/minified/polyfiller.js'/>"></script>
        <script>
        $.webshims.polyfill('forms forms-ext');
        </script>
    </jsp:attribute>
</sage:main>
