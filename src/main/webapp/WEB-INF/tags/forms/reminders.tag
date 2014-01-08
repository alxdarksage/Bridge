<%@ include file="../../jsp/directives.jsp" %>
<div class="form-group">
	<sage:reminders-due/>
	<sage:reminders-direct/>
	<sage:reminders-always/>
	<small>
		<sage:comma-list first="No reminders for " items="${descriptorsNoPrompt}" separator=", " endSeparator=" or " last=""
			>${item.description}</sage:comma-list>
	</small>
</div>
