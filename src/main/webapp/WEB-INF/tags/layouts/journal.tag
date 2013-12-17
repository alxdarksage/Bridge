<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<sage:main code="${code}">
    <jsp:attribute name="navigation">
        <div class="panel panel-default">
            <div class="panel-heading"><b>Forms</b></div>
            <div class="panel-body">
                <ul class="list-group">
                    <li class="list-group-item">
                        <a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms.html">CBC</a>
                    </li>
                    <li class="list-group-item">
                        <a href="/bridge/journal/${sessionScope.BridgeUser.ownerId}/forms.html">Quality of Life Survey</a>
                    </li>
                </ul>                   
            </div>
        </div>    
    </jsp:attribute>
    <jsp:attribute name="content"><jsp:doBody/></jsp:attribute>
</sage:main>
