<%@ include file="../../directives.jsp" %>
<sage:admin code="AdminTrackers" active="Trackers">
    <div class="sage-crumbs">
        <a href="<c:url value="/admin/index.html"/>"><spring:message code="AdminDashboard"/></a> &#187; 
    </div>
    <h3><spring:message code="Trackers"/></h3>
    
    <c:if test="${not empty messages}">
        <p>${messages}</p>
    </c:if>
    
    <sage:table formId="trackersForm" action="/admin/trackers/index.html" itemId="id" items="${descriptors}" caption="Trackers">
        <sage:table-button id="updateAct" type="primary" label="Update Trackers" action="/admin/trackers/update.html"/> 
        <sage:column label="Name" field="name" className="nowrap"/>
        <sage:column label="Description" field="description"/>
    </sage:table>
</sage:admin>