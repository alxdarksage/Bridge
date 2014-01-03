<%@ include file="../../directives.jsp" %>
<sage:admin code="AdminCommunity" active="Communities">
    <div class="sage-crumbs">
        <a href="<c:url value="/admin/index.html"/>"><spring:message code="AdminDashboard"/></a> &#187; 
        <a href="<c:url value="/admin/communities/index.html"/>"><spring:message code="Communities"/></a> &#187;
    </div>
    <h3>New Community</h3>
    
    <%@ include file="_form.jsp" %>
    
</sage:admin>
