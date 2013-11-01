<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="true" %>
<div class="header row">
    <div class="col-sm-6 community-header">
        ${title}
    </div>
    <div class="col-sm-6 portal-header">
        <div class="portal-subheader visible-sm visible-md visible-lg">
            <a href="<c:url value='/portal/index.html'/>">Bridge Community Portal</a>
        </div>
        <c:if test="${title != 'Sign In'}">
            <div class="portal-links visible-xs visible-sm">
                <c:choose>
                    <c:when test="${sessionScope.BridgeUser.isAuthenticated()}">
                        <a href='<c:url value="/signOut.html"/>'>Sign Out</a> | 
                        <a href='<c:url value="/index.html"/>'>Bridge</a>
                    </c:when>
                    <c:otherwise>
                        <a href='<c:url value="/signIn.html"/>'>Sign In</a> |
                        <a href='<c:url value="/signUp.html"/>'>Sign Up</a> | 
                        <a href='<c:url value="/index.html"/>'>Bridge</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>
</div>
