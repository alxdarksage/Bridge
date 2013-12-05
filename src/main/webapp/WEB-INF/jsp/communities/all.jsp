<%@ include file="../directives.jsp" %>
<sage:community-wiki-edit code="${community.name}">
    <c:url var="editUrl" value="/communities/${community.id}/wikis/${wiki.id}/edit.html"/>
    <ul class="nav nav-tabs wiki-tabs">
        <li><a href="${editUrl}">${wiki.title}</a></li>
        <li class="active"><a><spring:message code="AllPages"/></a></li>
    </ul>
    
    <s:table formId="wikisForm" action="/communities/${community.id}/wikis/${wiki.id}/all.html" itemId="id" items="${wikiHeaders}" caption="Wiki Pages">
        <s:button id="newWikiAct" type="primary" label="New Page" action="/communities/${community.id}/wikis/new.html"/>

        <%-- Cannot disable because I don't have expression evaluation implemented there. --%>            
        <s:column label="Name" field="title" link="/communities/${community.id}/wikis/{id}/edit.html"/>
        
        <s:button id="deleteAct" type="danger" label="Delete" action="delete"/>
    </s:table>
</sage:community-wiki-edit>
