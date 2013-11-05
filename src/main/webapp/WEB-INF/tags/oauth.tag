<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <div style="text-align: center; margin-top: 2rem;">
        <span style="color: #aaa; text-decoration: line-through">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</span> OR <span style="color: #aaa; text-decoration: line-through">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</span>
    </div>
    <form action="/webapp/openId.html" method="post">
        <input name="OPEN_ID_PROVIDER" type="hidden" value="GOOGLE"/>
        <input name="RETURN_TO_URL" type="hidden" value="${sessionScope['origin']}"/>
        <input name="MODE" type="hidden" value="STANDARD">
        <div style="text-align: center; margin-top: 1rem; margin-bottom: -.5rem">
           <button type="submit" style="max-width: 100%; -webkit-appearance: none; border: none; background: none;">
               <img src='<c:url value="/images/google-sign-in.png"/>' style="max-width: 100%"/>
           </button>
        </div>
    </form>
