<%@page import="org.sagebionetworks.bridge.model.data.value.ParticipantDataDoubleValue"%>
<%@include file="../directives.jsp" %>
<%
	ParticipantDataDoubleValue currentValue = (ParticipantDataDoubleValue)request.getAttribute("currentValue");
	ParticipantDataDoubleValue previousValue = (ParticipantDataDoubleValue)request.getAttribute("previousValue");
	Double value = currentValue != null ? currentValue.getValue() : (previousValue != null ? previousValue.getValue() : 0.5);
	boolean isCurrent = (currentValue != null);
%>
<c:set var="id" value="${trackerId}-${columnDescriptor.name}"/>
${columnDescriptor.name}:<br/>
<input type="text" id="${id}-slider" class="slider" value=""
	data-slider-min="0" data-slider-max="1" data-slider-step="0.1"
	data-slider-value="0.5" data-slider-orientation="horizontal"
	data-slider-selection="after" data-slider-tooltip="hide">
<input type="hidden" id="${id}-value" name="valuesMap['${columnDescriptor.name}']" value="<%= value %>"/>
<div id="${id}-out"></div>
<script type="text/javascript">
	var slider = new SmileySlider(document.getElementById("${id}-slider"));
	slider.setQuestion(<%= !isCurrent %>);
	slider.position(<%= value %>);
    slider.position(function (p) {
        var mySlider = this;
    	var form = $("#${trackerId}");
    	var valueInput = $("#${id}-value");
    	var rowIdInput = $("#${trackerId}-rowId");

    	valueInput.val(p);
    	var rowId = rowIdInput.val();
    	var url = form.attr("action") + "/ajax" + (rowId == '' ? '/new' : '/row/' + rowId) + ".html";
        var serialized = $("#${trackerId}").serialize();
        serialized += '&' + $.param({"valuesMap['date']":new Date().toISOString().replace(/T.*/,"")});
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
