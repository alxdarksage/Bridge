<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
	<title>Sign In</title>
	<link rel="stylesheet" type="text/css" href="assets/header.css" />
</head>
<body>
	<div class="container" style="width: 300px; margin: 0 auto">
		<h3>Sign In</h3>
		<form:form role="form" modelAttribute="signInForm" method="post">
			<spring:bind path="userName">
				<div class="form-group ${status.error ? 'has-error' : ''}">
					<label class="control-label" for="username">User Name</label>
					<form:input cssClass="form-control" id="userName" path="userName"/>
					<form:errors path="userName" />
	  			</div>
			</spring:bind>
			<spring:bind path="password">
				<div class="form-group ${status.error ? 'has-error' : ''}">
					<label class="control-label" for="password">Password</label>
					<form:password cssClass="form-control" id="password" path="password"/>
					<form:errors path="password" />
	  			</div>
			</spring:bind>
			<button type="submit" class="btn btn-default">Sign In</button>
		</form:form>
	</div>
    <script type="text/javascript" src="assets/footer.js"></script>
</body>
</html>
