<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="code" required="true" %>
<%@ attribute name="boxSize" required="false" %> <%-- 70rem by default --%>
<%@ attribute name="mobile" required="true" %>
<!DOCTYPE html>
<html lang="en">
<sage:head code="${code}"/>
<body>
    <c:choose>
        <c:when test="${mobile}">
		    <sage:mobile-nav>
		        <div class="container">
		            <sage:header code="${code}" mobile="true"/>
		            <div class="row main-pane">
		                <c:choose>
		                    <c:when test="${boxSize != ''}">
		                        <div class="box" style="max-width: ${boxSize}"><jsp:doBody/></div>
		                    </c:when>
		                    <c:otherwise>
		                        <div class="box"><jsp:doBody/></div>
		                    </c:otherwise>
		                </c:choose>
		            </div>
		            <sage:footer/>
		        </div>
		    </sage:mobile-nav>
        </c:when>
        <c:otherwise>
            <div class="container">
                <sage:header code="${code}" mobile="false"/>
                <div class="row main-pane">
                    <c:choose>
                        <c:when test="${boxSize != ''}">
                            <div class="box" style="max-width: ${boxSize}"><jsp:doBody/></div>
                        </c:when>
                        <c:otherwise>
                            <div class="box"><jsp:doBody/></div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <sage:footer/>
            </div>
        </c:otherwise>
    </c:choose>
    <script src="<c:url value='/assets/footer.js'/>"></script>
    <sage:notice/>
</body>
</html>