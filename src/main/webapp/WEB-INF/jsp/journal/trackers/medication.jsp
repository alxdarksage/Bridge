<%@ include file="../../directives.jsp" %>
<sage:modal id="change-dose" title="Change dosage" action="/bridge/medication/${descriptor.id}/row/#rowid#/ajax/changeDosage">
	<input type="hidden" class="medication-value" name="valuesMap['medication']" value=""/>
	<div class="row">
		<div class="col-md-4">New dosage</div>
		<div class="col-md-8"><input type="text" required class="dose-value" name="valuesMap['dose']" value=""/> <i>(was <span class="old-dosage"></span>)</i></div>
	</div>
	<div class="row">					
		<div class="col-md-4">Since:</div>
		<div class="col-md-8"><sage:date name="valuesMap['start_date']" clazz="date-picker" lastweek="true" lastmonth="true" defaultToday="true"/></div>
	</div>
</sage:modal>
<sage:modal id="end-med" title="No longer taking" action="/bridge/medication/${descriptor.id}/row/#rowid#/ajax/close">
	<div class="row">					
		<div class="col-md-4">Since:</div>
		<div class="col-md-8"><sage:date name="valuesMap['end_date']" clazz="date-picker" lastweek="true" lastmonth="true" defaultToday="true"/></div>
	</div>
	<div class="row">
		<div class="error"></div>
	</div>
</sage:modal>
<script type="text/javascript">
$(function(){
	var changeDoseModal = $('#change-dose');
	var changeDoseForm = changeDoseModal.find('form');

	changeDoseModal.on('show.bs.modal', function (event) {
		var row = $(event.relatedTarget).closest('tr');
		var rowId = row.data('rowid');
		changeDoseForm.data('rowid', rowId);
		resetDateToToday(changeDoseForm.find(".date-picker"));
		changeDoseForm.find(".old-dosage").text(row.find(".dose").text());
		changeDoseForm.find(".dose-value").val("");
		changeDoseForm.find(".medication-value").val(row.find(".medication").text());
		changeDoseForm.find(".dose-value").focus();
	});

	changeDoseModal.on('shown.bs.modal', function (event) {
		changeDoseForm.find(".dose-value").focus();
	});

	changeDoseModal.submit( function(event) {
		event.preventDefault();
		var id = changeDoseForm.data('rowid');
		var row = $('#cur-med-' + id);
		var url = changeDoseForm.attr("action") + ".html";
		url = url.replace(/#rowid#/, id);
	    var serialized = changeDoseForm.serialize();
	    $.ajax({
	        success: function(data) {
	        	document.location.reload();
	        },
	        error: function(req,error,status) {
	        	row.find(".error").text(status +": " + error);
	        },
	        type: "POST",
	        url: url,
	        data: serialized,
	        dataType: "json"
	    });
	});

	var endDoseModal = $('#end-med');
	var endDoseForm = endDoseModal.find('form');

	endDoseModal.on('show.bs.modal', function (event) {
		var row = $(event.relatedTarget).closest('tr');
		var rowId = row.data('rowid');
		endDoseForm.data('rowid', rowId);
		resetDateToToday(endDoseForm.find(".date-picker"));
	});

	endDoseModal.submit( function(event) {
		event.preventDefault();
		var id = endDoseForm.data('rowid');
		var row = $('#cur-med-' + id);
		var url = endDoseForm.attr("action") + ".html";
		url = url.replace(/#rowid#/, id);
	    var serialized = endDoseForm.serialize();
	    $.ajax({
	        success: function(data) {
	        	document.location.reload();
	        },
	        error: function(req,error,status) {
	        	row.find(".error").text(status +": " + error);
	        },
	        type: "POST",
	        url: url,
	        data: serialized,
	        dataType: "json"
	    });
	});

	$('.new-med-ok').closest("form").submit( function(event) {
		event.preventDefault();
		var form = $(event.target).closest("form");
		var url = form.attr("action") + ".html";
	    var serialized = form.serialize();
	    $.ajax({
	        success: function(data) {
	        	document.location.reload();
	        },
	        error: function(req,error,status) {
	        	form.find(".error").text(status +": " + error);
	        },
	        type: "POST",
	        url: url,
	        data: serialized,
	        dataType: "json"
	    });
	});
});
</script>

<h3>Current medications and supplements</h3>
<form action="/bridge/medication/${descriptor.id}/ajax/new" data-rowid="">
	<table class="table">
		<caption>Current medications and supplements</caption>
		<thead>
			<tr>
				<th>Name</th>
				<th>Dosage</th>
				<th>Started</th>
				<th></th>
			</tr>
		</thead>
		<tbody class="dataRows">
			<tr>
				<td><input type="text" autofocus required class="medication-value" name="valuesMap['medication']" value=""/></td>
				<td class="dose"><input type="text" required class="dose-value" name="valuesMap['dose']" value=""/></td>
				<td class="start">
					<sage:date name="valuesMap['start_date']" clazz="date-picker" lastweek="true" lastmonth="true" defaultToday="true"/>
				</td>
				<td>
					<button type="submit" class="btn btn-sm new-med-ok">Save new medication or supplement</button>
					<div class="error"></div>
				</td>
			</tr>
			<c:forEach var="row" items="${current}" varStatus="loop">
				<tr id="cur-med-${row.rowId}" data-rowid="${row.rowId}">
					<td class="medication">${row.data.medication.value}</td>
					<td class="dose">${row.data.dose.value}</td>
					<td class="start"><sage:formatDate value="${row.data.start_date.value}"/></td>
					<td>
						<div>
							<button type="button" class="btn btn-sm med-change-dose" data-toggle="modal" href="#change-dose">dosage changed</button>
							<button type="button" class="btn btn-sm med-end-med" data-toggle="modal" href="#end-med">no longer taking</button>
						</div>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</form>
<br/>
<br/>
<h3>Medication and supplement history</h3>
<table class="table">
	<caption>Medication and supplement history</caption>
	<thead>
		<tr>
			<th>Name</th>
			<th>Dosage</th>
			<th>Started</th>
			<th>Ended</th>
			<th></th>
		</tr>
	</thead>
	<tbody class="dataRows">
		<c:forEach var="rowOfRows" items="${past}" varStatus="loop">
			<c:forEach var="row" items="${rowOfRows}" varStatus="loop2">
				<c:choose>
					<c:when test="${loop2.first && loop2.last}">
						<tr>
					</c:when>
					<c:when test="${loop2.last}">
						<tr class="group-last">
					</c:when>
					<c:when test="${loop2.first}">
						<tr class="group-first">
					</c:when>
					<c:otherwise>
						<tr class="group-middle">
					</c:otherwise>
				</c:choose>
					<td>
						<c:if test="${loop2.first}">
							${row.data.medication.value}
						</c:if>
					</td>
					<td>${row.data.dose.value}</td>
					<td><sage:formatDate value="${row.data.start_date.value}"/></td>
					<td><sage:formatDate value="${row.data.end_date.value}"/></td>
					<td><!-- <div><a class="btn btn-sm" href="#">edit</a><a class="btn btn-sm" href="#">delete</a></div> --></td>
				</tr>
			</c:forEach>
		</c:forEach>
	</tbody>
</table>
