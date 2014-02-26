<%@ include file="../directives.jsp" %>
<%--
    This is just some junk thrown in, mostly to point out that we need to think through what will be here.
    Feel free to throw away what is here.
 --%>
<sage:portal code="Tagline">
    <div class="row">
        <div class="col-md-3">
            <sage:signIn/>
        </div>
        <div class="col-md-6">
            <c:url var="imageUrl" value="/static/images/bridge.jpg"/>
            <div style="margin-bottom: 1rem; outline: 1px solid #aaa; position: relative; background: transparent url('${imageUrl}') 4% 48% no-repeat; max-width: 600px; height: 200px ! important">&#160;</div>
            <p id="portal-page">
                This is the portal page. It has anonymous access, but needs to know if you are authenticated or not. 
                It also needs content of some kind. The current appearance is intended to be slightly embarassing, 
                as a call to action. 
            </p>
        </div>
        <div class="col-md-3">
            <ul class="list-group" id="communities">
                <c:forEach var="cty" items="${communities}">
                    <li class="list-group-item"><a href='<c:url value="/communities/${cty.id}.html"/>'>${cty.name}</a></li>
                </c:forEach>
            </ul>
        </div>
    </div>
    <p style="border-top: 1px solid #aaa; margin-top: 2rem">
        <c:if test="${pageContext.request.isUserInRole('admin')}">
            <a id="adminAct" href='<c:url value="/admin/"/>'>Administration</a>
        </c:if>
    </p>
</sage:portal>
