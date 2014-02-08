<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="element" required="true" type="org.sagebionetworks.bridge.webapp.specs.FormElement" %>

<h3>${element.label}</h3>
<c:choose>
    <c:when test="${not empty requestScope[element.modelName]}">
        <c:url var="tabUrl" value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}.html"/>
        <form role="role" method="post" action="${tabUrl}" id="tabularForm">
	        <table id="${element.modelName}Table" width="100%" class="table table-selectable smBottomSpaced">
	            <thead>
	                <tr>
	                    <th class="checkRow"></th>
	                    <c:forEach var="el" items="${element.children}">
	                        <th>
	                            <sage:form-label field="${el}"/>
	                        </th>
	                    </c:forEach>
	                </tr>
	            </thead>
	            <tbody class="dataRows">
	                <c:forEach var="row" items="${requestScope[element.modelName]}">
	                    <tr data-id="${row.rowId}">
	                        <td>
	                            <input type="checkbox" name="rowSelect" title="Select Row" value="${row.rowId}" class="user-success">
	                        </td>
	                        <c:forEach var="el" items="${element.children}">
	                            <td data-title="${el.label}: " style="width:${100 / fn:length(element.children)+1}%">
	                                <c:set var="dynamicForm" value="${sage:valuesMapHolder(form.showStructure, row)}" scope="request" />
	                                <sage:router element="${el}"/>
	                            </td>
	                        </c:forEach>
	                    </tr>
	                </c:forEach>
	            </tbody>
	            <tfoot>
	                <tr>
	                    <td><input type="checkbox" name="masterSelect" title="Select All Rows"></td>
	                    <td colspan="${fn:length(element.children)}">
	                        <button type="submit" id="deleteAct" name="delete" value="delete" 
	                            class="btn btn-xs disabled btn-danger" 
	                            data-confirm="Are you sure you wish to delete this data?">Delete</button>
	                    </td>
	                </tr>
	            </tfoot>            
	        </table>
        </form>
    </c:when>
    <c:otherwise>
        <table id="${element.modelName}Table" width="100%" class="table table-selectable smBottomSpaced">
            <thead>
                <tr>
                    <th class="checkRow"></th>
                    <c:forEach var="el" items="${element.children}">
                        <th><sage:form-label field="${el}"/></th>
                    </c:forEach>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td class="empty" colspan="${fn:length(element.children)+1}">
                        There are no ${fn:toLowerCase(element.label)}.
                    </td>
                </tr>
            </tbody>
        </table>          
    </c:otherwise>
</c:choose>
<script>
window.addEventListener("DOMContentLoaded", function() {
    var template = "<c:url value="/journal/${sessionScope.BridgeUser.ownerId}/trackers/${descriptor.id}/ajax/row/@/nostatuschange.html"/>";
    var fields =  $('#activeTable input[type=date]');

    setInterval(function() {
        fields.each(function() {
        	if (this.value) {
                this.readonly = true;
                var row = $(this).closest("tr"),
                    rowId = row.data("id"),
                    url = template.split("@").join(rowId),
                    query = $(this).attr('name') + "=" + encodeURIComponent($(this).val());
                
                humane.log("Updating...");
                $.ajax(url, {
                    method: "post",
                    data: query
                }).success(function(params) {
                    document.location.reload();
                });
            }
        });
    }, 300);
});
</script>