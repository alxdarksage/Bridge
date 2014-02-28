<%@ include file="../jsp/directives.jsp" %>
<div class="footer-break">
    <p>
        &copy; 2014 <a href="http://www.sagebase.org">Sage Bionetworks</a> 
        <c:if test="${pageContext.request.isUserInRole('admin')}">
            &bull; <a id="adminAct" href='<c:url value="/admin/"/>'>Administration</a>
        </c:if><br/>
        <span><small>bridge: ${applicationScope.bridgeVersion}, repo: ${applicationScope.repoVersion}</small></span>
    </p>
</div>
