<%@ include file="../directives.jsp" %>
<sage:admin code="AdminCommunities" active="Communities">
    <div class="sage-crumbs">
        <a href="index.html">Admin Dashboard</a> &#187; 
    </div>
    <h3>Communities</h3>
    <s:table formId="communityForm" action="/admin/communities.html" itemId="id" items="${communities}" caption="Communities">
        <s:button id="newCommunityAct" type="primary" label="New Community" action="/admin/communities/new.html"/>
        <s:column label="Name" field="name" link="/admin/communities/{id}.html"/>
        <s:column label="Description" field="description"/>
        <s:column label="Created" field="createdOn"/>
        <s:column label="" icon="eye-open" static="View" link="/communities/{id}.html"/>
        <s:button id="deleteAct" type="danger" label="Delete" action="delete"/>
    </s:table>
</sage:admin>
