<%@ include file="../../directives.jsp" %>
<sage:admin code="Patient Data Descriptors Administration" active="PDD">
    <div class="sage-crumbs">
        <a href="<c:url value="/admin/index.html"/>">Admin Dashboard</a>
    </div>
    <h3>Patient Data Descriptors</h3>
    
    <c:url var="pddCreateUrl" value="/admin/descriptors/create/cbc.html"/>
    <p><a id="cbcAct" href="${pddCreateUrl}">Click here to create a CBC descriptor.</a></p>
</sage:admin>
