<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="navigation" required="true" fragment="true" %>
<%@ attribute name="content" required="true" fragment="true" %>
<%@ attribute name="scripts" required="false" fragment="true" %>
<!DOCTYPE html>
<html lang="en">
<sage:head code="${code}"/>
<body>
    <div class="container">
        <sage:header code="${code}"/>
        <div class="row main-pane">
            <div id="nav-pane" class="col-sm-3 visible-sm visible-md visible-lg">
                <sage:signIn/>
                <jsp:invoke fragment="navigation" />
            </div>
            <div id="content-pane" class="col-sm-9">
                <jsp:invoke fragment="content" />
            </div>
        </div>
    </div>
    <script type="text/javascript" src="<c:url value='/assets/footer.js'/>"></script>
    <jsp:invoke fragment="scripts" />
    <sage:footer/>
</body>
</html>