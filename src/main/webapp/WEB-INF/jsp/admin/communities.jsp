<%@ include file="../directives.jsp" %>
<sage:admin code="AdminDashboard" active="Communities">
    <h5>
        <a href="index.html">Admin Dashboard</a> &#187; 
    </h5>
    <h3>Communities</h3>
    <s:table formId="communityForm" action="/admin/communities.html" itemId="id" items="${communities}" caption="Communities">
        <s:button id="newCommunityButton" type="primary" label="New Community" action="/admin/communities/new.html"/>
        <s:column label="Name" field="name" link="/admin/communities/{id}.html"/>
        <s:column label="Description" field="description"/>
        <%--<s:button id="deleteButton" type="danger" label="Delete" action="delete"/> --%>
    </s:table>
</sage:admin>
