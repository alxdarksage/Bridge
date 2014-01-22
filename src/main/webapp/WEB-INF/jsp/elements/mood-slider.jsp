<%@page import="org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue"%>
<%@include file="../directives.jsp" %>
<%
	Object trackerId = request.getAttribute("trackerId");
	Object columnDescriptor = request.getAttribute("columnDescriptor");
	ParticipantDataDoubleValue value = (ParticipantDataDoubleValue)request.getAttribute("value");
%>
<c:set var="id" value="${trackerId}-${columnDescriptor.name}"/>
${columnDescriptor.name}<span id="${id}-slider"></span>
<input type="hidden" id="${id}-value" name="valuesMap['${columnDescriptor.name}']" value="${empty value ? 0.5 : value.getValue()}"/>
<div id="${id}-out"></div>
<script type="text/javascript">
    var slider = new SmileySlider(document.getElementById("${id}-slider"));
	slider.setQuestion();
	slider.position(${empty value ? 0.5 : value.getValue()});
    slider.position(function (p) {
        var mySlider = this;
    	var form = $("#${trackerId}");
    	var valueInput = $("#${id}-value");
    	var rowIdInput = $("#${trackerId}-rowId");

    	valueInput.val(p);
    	var rowId = rowIdInput.val();
    	var url = form.attr("action") + "/ajax" + (rowId == '' ? '/new' : '/row/' + rowId) + ".html";
        var serialized = $("#${trackerId}").serialize();
         $.ajax({
             success: function(data) {
                 rowIdInput.val(data.rowId);
                 mySlider.removeQuestion();
             },
             error: function(req,error,status) {
            	 $("#${id}-out").text(status +": " + error);
             },
             type: "POST",
             url: url,
             data: serialized,
             dataType: "json"
         });
    });
</script>
