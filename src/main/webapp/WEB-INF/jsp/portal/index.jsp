<%@ include file="../directives.jsp" %>
<sage:portal code="Tagline">
    <p id="portal-page">
        This is the portal page. It isn't i18nalized. It has anonymous access, but needs to know if you are authenticated or not.
        We'll see if getCommunities() works when you are not authenticated. 
    </p>
    <ul>
        <c:forEach var="cty" items="${communities}">
            <li><a href='<c:url value="/communities/${cty.id}.html"/>'>${cty.name}</a></li>
        </c:forEach>
    </ul>
    <p><a href='<c:url value="/admin/"/>'>Admin</a></p>
</sage:portal>
