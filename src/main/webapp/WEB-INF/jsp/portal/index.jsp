<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:portal title="Patients &amp; Researchers in Partnership">
    <p id="portal-page">
        This is the portal page. It has anonymous access, but needs to know if you are authenticated or not. 
    </p>
    
    <ul>
        <li><a href='<c:url value="/communities/index.html"/>'>Fanconi Anemia</a></li>
    </ul>
</sage:portal>

