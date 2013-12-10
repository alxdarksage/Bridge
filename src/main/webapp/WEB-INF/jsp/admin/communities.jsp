<%@ include file="../directives.jsp" %>
<sage:admin code="AdminCommunities" active="Communities">
    <div class="sage-crumbs">
        <a href="index.html">Admin Dashboard</a> &#187; 
    </div>
    <h3>Communities</h3>
    <sage:table formId="communityForm" action="/admin/communities.html" itemId="id" items="${communities}" caption="Communities">
        <sage:button id="newCommunityAct" type="primary" label="New Community" action="/admin/communities/new.html"/>
        <sage:column label="Name" field="name" link="/admin/communities/{id}.html" className="nowrap"/>
        <sage:column label="Description" field="description"/>
        <sage:column label="Created" field="createdOn"/>
        <sage:column label="" icon="eye-open" static="View" link="/communities/{id}.html" className="nowrap"/>
        <sage:button id="deleteAct" type="danger" label="Delete" action="delete"/>
    </sage:table>
</sage:admin>
