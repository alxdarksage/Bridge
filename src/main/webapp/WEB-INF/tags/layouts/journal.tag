<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<sage:main code="${code}">
    <jsp:attribute name="navigation">
        <div class="panel panel-default">
            <div class="panel-heading"><b>Forms</b></div>
            <div class="panel-body">
                <ul class="list-group">
                    <c:forEach var="descriptor" items="${descriptors}">
	                    <li class="list-group-item">
	                        <a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms/${descriptor.id}.html">
	                           ${(not empty descriptor.description) ? descriptor.description : descriptor.name}
                           </a>
	                    </li>
                    </c:forEach>
                </ul>                   
            </div>
        </div>    
    </jsp:attribute>
    <jsp:attribute name="content"><jsp:doBody/></jsp:attribute>
</sage:main>
