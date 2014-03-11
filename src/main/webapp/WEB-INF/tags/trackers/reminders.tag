<%@ include file="../../jsp/directives.jsp" %>
<div class="form-group">
	<sage:reminders-due/>
	<sage:reminders-always/>
	<sage:reminders-medication/>
	<small>
		<sage:comma-list first="No reminders for " items="${descriptorsNoPrompt}" separator=", " endSeparator=" or " last=""
			>${item.description}</sage:comma-list>
	</small>
</div>
