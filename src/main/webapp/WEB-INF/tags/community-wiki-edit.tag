<%@ include file="../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<sage:main code="${code}">
    <jsp:attribute name="navigation">
        <div class="panel panel-default">
            <div class="panel-heading">
                Community Page Navigation
            </div>
            <div class="panel-body">
                <em>Nav here</em>
            </div>
            <div class="panel-footer">
                <a href="" class="btn btn-small btn-default">Edit</a>
            </div>
        </div>
    </jsp:attribute>
    <jsp:attribute name="content"><jsp:doBody/></jsp:attribute>
</sage:main>
