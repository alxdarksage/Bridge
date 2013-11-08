<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sage" tagdir="/WEB-INF/tags" %>
<sage:minimal title="">
    <h3 id="error-pane">${requestScope.title}</h3>
    <p>We&#8217;re sorry, Bridge encountered an issue and couldn&#8217;t fulfill 
    your request.</p>

    <p>If you try again and the problem persists, please 
    <a href="mailto:synapseInfo@sagebase.org?subject=Bridge Error">contact us</a> 
    and let us know. Thanks!</p>
    
    <script>console.error("${requestScope.message}");</script>        
</sage:minimal>
