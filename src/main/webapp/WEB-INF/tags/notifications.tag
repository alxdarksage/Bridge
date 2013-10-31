<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${requestScope['notice']}">
    <script>humane.log("${requestScope['notice']}");</script>
</c:if>