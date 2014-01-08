<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="content" required="true" fragment="true" %>
<%@ attribute name="scripts" required="false" fragment="true" %>
<sage:main code="${code}">
    <jsp:attribute name="navigation">
        <div class="panel panel-default">
            <div class="panel-heading"><b>Forms</b></div>
            <div class="panel-body">
                <ul class="list-group">
                    <c:forEach var="descriptor" items="${descriptors}">
	                    <li class="list-group-item">
	                        <a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html">
	                           ${(not empty descriptor.name) ? descriptor.name : descriptor.description}
                           </a>
	                    </li>
                    </c:forEach>
                </ul>                   
            </div>
        </div>    
    </jsp:attribute>
    <jsp:attribute name="content"><jsp:invoke fragment="content" /></jsp:attribute>
    <jsp:attribute name="scripts">
        <script src="<c:url value='/assets/journal.js'/>"></script>
        <script>$("#dp3").datepicker();</script>
    </jsp:attribute>
</sage:main>
