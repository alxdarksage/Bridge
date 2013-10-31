<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags/layouts" %>
<%@ taglib prefix="sgf" tagdir="/WEB-INF/tags/form" %>
<layout:minimal title="Sign Up for Bridge" boxSize="40rem">
    <p>It&#8217;s simple to get started, just enter your email address! </p>
    
    <c:url var="signUpUrl" value="/signUp.html"/>
    <form:form role="form" modelAttribute="signUpForm" method="post" action="${signUpUrl}">
        <sgf:text field="email" label="Your email address"/>
        <button type="submit" class="btn btn-sm btn-default">Sign Up</button>
    </form:form>
</layout:minimal>
