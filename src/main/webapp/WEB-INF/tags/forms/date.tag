<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="clazz" required="true" %>
<%@ attribute name="lastweek" type="java.lang.Boolean" required="false" %>
<%@ attribute name="lastmonth" type="java.lang.Boolean" required="false" %>
<%@ attribute name="inline" type="java.lang.Boolean" required="false" %>
<%@ attribute name="defaultToday" type="java.lang.Boolean" required="false" %>
<c:if test="${empty defaultToday || defaultToday}">
	<c:set var="datePickerToday" value="date-picker-today"></c:set>
</c:if>
<div>
	<input type="date" name="${name}" class="${clazz} ${datePickerToday}"/>
	<br/>
	<c:if test="${lastweek}">
		<a class="date-set last-week" tabindex="-1" href="#">Last week</a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${lastmonth}">
		<a class="date-set last-month" tabindex="-1" href="#">Last month</a>
	</c:if>
</div>
<sage:jsonce id="date.tag">
	<script type="text/javascript">
		$( function() {
			$(".date-picker-today").val(new Date().toJSON().slice(0,10));
			$(".date-set").click( function(event) {
				var date = new Date();
				if ( $(event.target).hasClass("last-week") ) {
					date = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);
				} else if ( $(event.target).hasClass("last-month") ) {
					date = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);
				}
				$(event.target).parent().find("input").val(date.toJSON().slice(0,10));
			});
		});
		function resetDateToToday(element){
			element.val(new Date().toJSON().slice(0,10));
		}
	</script>
</sage:jsonce>
