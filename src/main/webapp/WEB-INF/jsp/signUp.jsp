<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:minimal title="Sign Up for Bridge" boxSize="40rem">
    <p>It&#8217;s simple to get started! </p>
    <c:url var="signUpUrl" value="/signUp.html"/>
        
    <spring:bind path="signUpForm">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger">
                <form:errors path="signUpForm"></form:errors>
            </div>
        </c:if>
    </spring:bind>
    <form:form role="form" modelAttribute="signUpForm" method="post" action="${signUpUrl}">
        <spring:hasBindErrors name="*">
            <div class="alert alert-danger">
                <form:errors></form:errors>
            </div>
        </spring:hasBindErrors>
        <sage:text field="email" label="Your email address"/>
        <sage:text field="displayName" label="Display name">
            <span class="help-block">For example, &#147;tinkerbell&#148;. Don&#8217;t user your real name 
            if you wish to remain anonymous (Bridge won&#8217;t show your email address to other users). </span>
        </sage:text>
        <sage:checkbox field="acceptsTermsOfUse">
            I agree to the <a href='<c:url value="/tos.html"/>' target="_blank">terms &amp; conditions</a>
        </sage:checkbox>
        <button type="submit" class="btn btn-sm btn-default">Sign Up</button>
    </form:form>
</sage:minimal>
