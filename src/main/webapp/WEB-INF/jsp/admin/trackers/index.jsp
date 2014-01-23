<%@ include file="../../directives.jsp" %>
<sage:admin code="AdminCommunities" active="Communities">
    <div class="sage-crumbs">
        <a href="<c:url value="/admin/index.html"/>"><spring:message code="AdminDashboard"/></a> &#187; 
    </div>
    <h3><spring:message code="Trackers"/></h3>
    <sage:table formId="trackersForm" action="/admin/trackers/index.html" itemId="id" items="${descriptors}" caption="Trackers">
        <sage:table-button id="createAct" type="primary" label="Create Trackers" action="/admin/trackers/create.html"/> 
        <sage:column label="Name" field="name" className="nowrap"/>
        <sage:column label="Description" field="description"/>
        <sage:table-button id="deleteAct" type="danger" label="Delete" action="delete" confirm="Are you sure you wish to delete this tracker?"/>
    </sage:table>
</sage:admin>