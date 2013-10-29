<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<layout:minimal title="Sign In">
	<div class="container" style="width: 300px; margin: 0 auto">
		<h3>Sign In</h3>
		<form:form role="form" modelAttribute="signInForm" method="post">
			<sage:text-input field="userName" label="User Name"/>
			<sage:text-input field="password" label="Password"/>
			<button type="submit" class="btn btn-default">Sign In</button>
		</form:form>
	</div>
</layout:minimal>
