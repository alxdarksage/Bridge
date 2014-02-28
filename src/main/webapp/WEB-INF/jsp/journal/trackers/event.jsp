<%@page import="org.sagebionetworks.bridge.model.data.value.ParticipantDataEventValue"%>
<%@ include file="../../directives.jsp" %>
<sage:modal id="end-event" title="Event ended" action="/bridge/event/${descriptor.id}/row/#rowid#/ajax/close">
	<div class="row">					
		<div class="col-md-4">When:</div>
		<div class="col-md-8"><sage:date name="valuesMap['event-end']" clazz="date-picker" lastweek="true" lastmonth="true" defaultToday="true"/></div>
	</div>
	<div class="row">
		<div class="error"></div>
	</div>
</sage:modal>
<script type="text/javascript">
$(function(){
	var endEventModal = $('#end-event');
	var endEventForm = endEventModal.find('form');

	endEventModal.on('show.bs.modal', function (event) {
		var row = $(event.relatedTarget).closest('tr');
		var rowId = row.data('rowid');
		endEventForm.data('rowid', rowId);
		resetDateToToday(endEventForm.find(".date-picker"));
	});

	endEventModal.submit( function(event) {
		event.preventDefault();
		var id = endEventForm.data('rowid');
		var row = $('#cur-event-' + id);
		var url = endEventForm.attr("action") + ".html";
		url = url.replace(/#rowid#/, id);
	    var serialized = endEventForm.serialize();
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

	$('.end input[type=checkbox]').click( function(event) {
		if ( !$(event.target).is(':checked') ) {
			$(event.target).closest("td").find(".date-picker").removeAttr('disabled');
		} else {
			$(event.target).closest("td").find(".date-picker").attr('disabled', 'disabled');
		}
	});

	$('.new-event-ok').closest("form").submit( function(event) {
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

<h3>Ongoing events</h3>
<form action="/bridge/event/${descriptor.id}/ajax/new" data-rowid="">
	<table class="table">
		<caption>Ongoing events</caption>
		<thead>
			<tr>
				<th>Event</th>
				<th>Details</th>
				<th>Started</th>
				<th>Ended</th>
				<th></th>
			</tr>
		</thead>
		<tbody class="dataRows">
			<tr>
				<td><input type="text" autofocus required class="event-value" name="valuesMap['event-name']" value=""/></td>
				<td class="details"><input type="text" class="details-value" name="valuesMap['details']" value=""/></td>
				<td class="start">
					<sage:date name="valuesMap['event-start']" clazz="date-picker" lastweek="true" lastmonth="true" defaultToday="true"/>
				</td>
				<td class="end">
					<sage:date name="valuesMap['event-end']" clazz="date-picker" lastweek="true" lastmonth="true" defaultToday="true"/>
					<label class="checkbox-inline"><input type="checkbox" class="event-ended">Ongoing</label>
				</td>
				<td>
					<button type="submit" class="btn btn-sm new-event-ok">Save new event</button>
					<div class="error"></div>
				</td>
			</tr>
			<c:forEach var="row" items="${current}" varStatus="loop">
				<tr id="cur-event-${row.rowId}" data-rowid="${row.rowId}">
					<td class="event">${row.data.event.name}</td>
					<td class="details">${row.data.details.value}</td>
					<td class="start"><sage:formatDate value="${row.data.event.start}"/></td>
					<td></td>
					<td>
						<div>
							<button type="button" class="btn btn-sm event-end-event" data-toggle="modal" href="#end-event">Ended</button>
						</div>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</form>
<br/>
<br/>
<h3>Event history</h3>
<table class="table">
	<caption>Event history</caption>
	<thead>
		<tr>
			<th>Event</th>
			<th>Details</th>
			<th>Started</th>
			<th>Ended</th>
			<th></th>
		</tr>
	</thead>
	<tbody class="dataRows">
		<%
			String lastGrouping = null;
		%>
		<c:forEach var="row" items="${past}" varStatus="loop">
			<tr id="hist-event-${row.rowId}">
				<td class="event">${row.data.event.name}</td>
				<td class="details">${row.data.details.value}</td>
				<td class="start"><sage:formatDate value="${row.data.event.start}"/></td>
				<td class="end"><sage:formatDate value="${row.data.event.end}"/></td>
				<td><!-- <div><a class="btn btn-sm" href="#">edit</a><a class="btn btn-sm" href="#">delete</a></div> --></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<a href="/bridge/event/${descriptor.id}/export.html" class="btn btn-sm btn-default">Export (*.csv)</a>
