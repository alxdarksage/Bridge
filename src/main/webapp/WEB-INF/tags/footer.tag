<%@ include file="../jsp/directives.jsp" %>
<c:if test="${sessionScope['notice'] != null}">
<script id="notice">humane.log("<spring:message code="${pageContext.request.getNotification()}"/>");</script>
</c:if>
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-46518505-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script> 