<%@ include file="../directives.jsp" %>
<sage:portal code="Tagline">
    <p id="portal-page">
        This is the portal page. It isn't i18nalized. It has anonymous access, but needs to know if you are authenticated or not. 
    </p>
    <ul>
        <li><a href='<c:url value="/communities/index.html"/>'>Fanconi Anemia</a></li>
        <!-- <li><a href='<c:url value="/admin/index.html"/>'>Admin Section</a></li> -->
    </ul>
</sage:portal>
