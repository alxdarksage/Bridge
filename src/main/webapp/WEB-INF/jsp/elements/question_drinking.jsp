<%@page import="org.sagebionetworks.bridge.model.data.ParticipantDataCurrentRow"%>
<%@include file="../directives.jsp" %>
<%
	ParticipantDataCurrentRow question = (ParticipantDataCurrentRow)request.getAttribute("question");
%>
<style>
.input-prompt {
  position: absolute;
  font-style: italic;
  color: #aaa;
  margin: 0.2em 0 0 0.5em;
}
.step {
	display: none;
	min-height: 200px;
	padding: 20px 20px 20px 20px;
}
.previous-answers {
	font-style: italic;
	padding: 20px 0px 20px 40px;
}
.answers div {
	padding: 20px 10px 20px 10px;
	cursor: pointer;
}
.answers div:nth-child(1) {
	border-color: #222;
	color: #222;
}
.answers div:nth-child(2) {
	border-color: #888;
	color: #888;
}
.answers div:nth-child(3) {
	border-color: #ccc;
	color: #ccc;
}
.answers div:nth-child(4) {
	border-color: #eee;
	color: #eee;
}
.answers div:nth-child(n+5) {
	display: none;
}
.inputs {
    display: table;
    width: 100% ! important;
    margin-bottom: .75rem;

    > * {
	    display: table-cell;
	    line-height: 30px;
	    height: 30px;
	    vertical-align: middle;
	    padding: 0;
    }
}
.subinputs {
	padding-top: 10px;
}
@media (max-width: 600px) {
	.inputs > * > button {
		display: block;
	    width: 100% ! important;
	    margin-bottom: .75rem;
	}
	.inputs > * > input {
		display: block;
	    width: 100% ! important;
	    margin-bottom: .75rem;
	}
}
</style>

<script type="text/javascript">
$(function(){

	var allFields = [ 
		<sage:comma-list first="" items="${question.columns}" separator=", " endSeparator=", " last="">
			"${item.name}"
	    </sage:comma-list>
	];

	var clickTarget = null;
	$("#form-${id} button").click( function(event) {
		clickTarget = $(event.target);
	});
	$("#form-${id}").submit( function(event) {
		event.preventDefault();
		var form = $(event.target);
		var step = clickTarget.closest("div[data-step]").data("step");
		var value = clickTarget.data("value");
		if ( typeof value == "undefined" ) {
			value = clickTarget.text();
		}
		var nextStep = step;
		var url = form.attr("action");
		rowId = form.find("input[name=rowId]").val();
		if ( step != "initial-changes" && rowId != "" ) {
			url = url + "row/" + rowId + "/ajax/update";
		} else {
			url = url + "ajax/new"
		}
		switch ( step ) {
		case "initial-first":
			// just submit as new
			formInput('drinker').val(value);
			if ( value == "Never" ) {
				nextStep = "done";
			} else {
				nextStep = "count_per_drinking_day";
			}
			break;
		case "initial-changes":
			// just submit as new
			if ( clickTarget.hasClass("yes-changes") ) {
				// clear and submit as new
				<c:forEach var="column" items="${question.columns}">
					<c:if test="${column.name != 'collected_on'}">
						formInput('${column.name}').val("");
					</c:if>
				</c:forEach>
				nextStep = "initial-first";
			} else if ( clickTarget.hasClass("no-changes") ) {
				// submit old values
				nextStep = "done";
			} else {
				alert("answer?");
			}
			break;
		case "count_per_drinking_day":
			formInput('count_per_drinking_day').val(value);
			nextStep = "frequency";
			break;
		case "frequency":
			formInput('frequency').val(value);
			nextStep = "kind";
			break;
		case "kind":
			if ( value == "Add" ) {
				value = clickTarget.parent().find("input").val();
			}
			formInput('kind').val(value);
			// we don't have to ask if we are still a drinker
			var lastDrinker = "${question.previousData.data[drinker].value}";
			var drinker = formInput('drinker').val();
			if ( lastDrinker == "Do" && drinker == "Do" ) {
				var lastCollected = "${question.previousData.data[collected_on].value}";
				var lastHowLongInMonths = "${question.previousData.data[how_long_in_months].value}";
				var lastHowLongInMonths = parseInt(lastHowLongInMonths);
				lastCollected = parseInt(lastCollected);
				var msPerMonth = (365.25/12) * 24 * 60 * 60 * 1000;
				var lastDrank = lastCollected - (lastHowLongInMonths * msPerMonth);
				lastDrank = lastDrank / (365.25/12) * 24 * 60 * 60 * 1000;
				formInput('how_long_in_months').val(lastDrank);
				nextStep = "other";
			} else {
				nextStep = "how_long_in_months";
			}
			break;
		case "how_long_in_months":
			// we don't have to ask if we are still a drinker
			var years = form.find("input[name=years]").val();
			var months = form.find("input[name=months]").val();
			var result = 0;
			if ( years != "" ) {
				result += parseInt(years) * 12;
			}
			if ( months != "" ) {
				result += parseInt(months);
			}
			formInput('how_long_in_months').val(result);
			var drinker = formInput('drinker').val();
			nextStep = (drinker == "Used to") ? "when_stopped" : "other";
			break;
		case "when_stopped":
			var date = form.find("input[name=stopped]").val();
			formInput('when_stopped').val(date);
			nextStep = "other";
			break;
		case "other":
			var other = form.find("input[name=other]").val();
			formInput('other').val(other);
			nextStep = "done";
			break;
		default:
			alert("step "+step+" not there");
			return;
		}
	    var today = new Date().toISOString().replace(/T.*/,"");
	    formInput('collected_on').val(today);
	    formInput('step').val(nextStep);
		var serialized = form.serialize();
        $.ajax({
             success: function(data) {
                 // add the previous answer (if any) to the answered questions
                 var answer = toStrings([step]);
                 if ( answer != "" ) {
                     pushAnswer(step,answer.join("<br/>"));
                 }
                 showStep(data.step);
         		 form.find("input[name=rowId]").val(data.rowId);
            	 //document.location.reload();
             },
             error: function(req,error,status) {
            	 $("#error").text(status +": " + error);
             },
             type: "POST",
             url: url + ".html",
             data: serialized,
             dataType: "json"
         });
    });

	$(document).on("click", ".answers div[data-answer]", function(event) {
		var clickedDiv = $(event.target);
		var step = $(event.target).data("answer");
		while ( clickedDiv.prev().length > 0 ) {
			clickedDiv.prev().remove();
		}
		clickedDiv.fadeOut("slow", function() { $(this).remove(); });
		showStep(step);
	});

	$(".subinputs input").keyup( function(event) {
		var disabled = ($(event.target).val() == "");
		$(event.target).parent().find("button").prop('disabled', disabled);
	});

	function toStrings(fields) {
		var result = [];
		var currentDrinker = true;
		for ( fieldIndex in fields) {
			var field = fields[fieldIndex];
			var value = formInput(field).val();
			if ( value != "" ) {
				switch ( field ) {
				case "collected_on":
					result.push("Last answer(s) on " + new Date(parseInt(value)).toLocaleDateString() + " were: ");
					break;
				case "drinker":
				case "initial-first":
				case "initial-changes":
					var drinker = value;
					if ( drinker == "Never" ) {
						return ["Never drank alcohol"];
					} else if ( drinker == "Used to" ) {
						result.push("Used to drink");
						currentDrinker = false;
					}
					break;
				case "count_per_drinking_day":
					result.push((currentDrinker ? "Drink" : "Drank") + " about " + uncapitalized(value) + " per drinking day");
					break;
				case "frequency":
					result.push((currentDrinker ? "Drink" : "Drank") + " " + uncapitalized(value));
					break;
				case "kind":
					result.push("Usually " + uncapitalized(value));
					break;
				case "how_long_in_months":
					if ( value != "0" ) {
						result.push((currentDrinker ? "Have been drinking" : "Drank for") + " about " + toYearsAndMonths(parseInt(value)));
					}
					break;
				case "when_stopped":
					if ( /[0-9]*/.test(value) ) {
						value = new Date(parseInt(value));
					} else {
						value = new Date(value);
					}
					result.push("Stopped drinking around " + toYearAndMonth(value));
					break;
				case "other":
					result.push("Additionally: " + value);
					break;
				}
			}
		}
		return result;
	}

	var currentStep = null;

	function showStep(step) {
		var step = step || formInput('step').val();
		if ( step == "" ) {
			step = "initial-changes";
		}
		if ( currentStep != null ) {
			showHideStep(currentStep, false);
		}
		currentStep = step;
		showHideStep(currentStep, true);
	}

	function showHideStep(step, showIt) {		
		var stepDiv = $("div[data-step=" + step + "]");
		var prevAnswerDiv = $(".previous-answers");
		if ( showIt ) {
			stepDiv.show();
			formInput('step').val(step);
			if ( step == "initial-changes" ) {
				prevAnswerDiv.html(toStrings(allFields).join("<br/>"));
			}
		} else {
			stepDiv.hide();
			prevAnswerDiv.html("");
		}
	}

	function pushAnswer(step,answer) {
		var div = "<div class='panel panel-default' data-answer='" + step + "'>" + answer + "</div>";
		$(div).hide().prependTo(".answers").fadeIn("slow");
	}

	function uncapitalized(str) {
		return str.substring(0,1).toLocaleLowerCase() + str.substring(1);
	}

	function toYearsAndMonths(monthCount) {
		var years = Math.floor(monthCount / 12);
		var months = monthCount % 12;
		var result = "";
		if ( years != "" ) {
			result += years + (years == "1" ? " year" : " years");
		}
		if ( months != "" ) {
			if ( result != "" ) {
				result += " and ";
			}
			result += months + (months == "1" ? " month" : " months");
		}
		return result;
	}

	function toYearAndMonth(date) {
		result = "";
		var monthNames = [ "January", "February", "March", "April", "May", "June",
		                   "July", "August", "September", "October", "November", "December" ];
        if ( date.getMonth() > 0 ) {
			result += monthNames[date.getMonth()];
			result += ", ";
		}
		result += date.getFullYear();
		return result;
	}

	function formInput(fieldName, form) {
		var form = form || $("#form-${id}");
		return form.find("input[name=valuesMap\\[\\'" + fieldName + "\\'\\]]");
	}

	var step = formInput("step").val();
	<c:choose>
		<c:when test="${empty question.previousData.data || (not empty question.previousData.data && not empty question.currentData.data && empty question.currentData.data.drinker)}">
			showStep("initial-first");
		</c:when>
		<c:otherwise>
			showStep(step == "" ? "initial-changes" : step);
		</c:otherwise>
	</c:choose>
});
</script>
<div class="panel panel-default">
	<form id="form-${id}" action="<c:url value="/question/${question.descriptor.id}/"/>">
		<input type="hidden" name="valuesMap['step']" value="${question.status.currentStep}"/>
		<input type="hidden" name="rowId" value="${question.currentData.rowId}"/>
		<c:forEach var="column" items="${question.columns}">
			<input type="hidden" name="valuesMap['${column.name}']" value="${empty question.currentData.data[column.name] ? question.previousData.data[column.name].value : question.currentData.data[column.name].value}"/>
		</c:forEach>
		<div class="step" data-step="initial-first">
			Do you or did you ever drink alcohol?
			<div class="inputs">
				<div>
					<button type="submit" class="btn btn-sm btn-info">Never</button>
					<button type="submit" class="btn btn-sm btn-info">Used to</button>
					<button type="submit" class="btn btn-sm btn-info">Do</button>
				</div>
			</div>
		</div>
		<div class="step" data-step="initial-changes">
			Are there any changes to your drinking habits since last time?
			<div class="previous-answers"></div>
			<div class="table-buttons">
				<div>
					<button type="submit" class="yes-changes btn btn-sm btn-info">Yes</button>
					<button type="submit" class="no-changes btn btn-sm btn-info">No</button>
				</div>
			</div>
		</div>
		<div class="step" data-step="count_per_drinking_day">
			How many drinks ${(question.currentData.data.drinker.value == "Used to") ? "did" : "do"} you usually consume on a drinking day?
			<div class="inputs">
				<div>
					<button type="submit" class="btn btn-sm btn-info">1</button>
					<button type="submit" class="btn btn-sm btn-info">2</button>
					<button type="submit" class="btn btn-sm btn-info">3</button>
					<button type="submit" class="btn btn-sm btn-info">4</button>
					<button type="submit" class="btn btn-sm btn-info">5 or more</button>
				</div>
			</div>
		</div>
		<div class="step" data-step="frequency">
			How frequently ${(question.currentData.data.drinker.value == "Used to") ? "did" : "do"} you drink alcohol?
			<div class="inputs">
				<div>
					<button type="submit" class="btn btn-sm btn-info">Daily</button>
					<button type="submit" class="btn btn-sm btn-info">Nearly every day</button>
					<button type="submit" class="btn btn-sm btn-info">3-4 times a week</button>
					<button type="submit" class="btn btn-sm btn-info">Twice a week</button>
					<button type="submit" class="btn btn-sm btn-info">Several time a month</button>
					<button type="submit" class="btn btn-sm btn-info">Several times a year</button>
				</div>
			</div>
		</div>
		<div class="step" data-step="kind">
			What kind of drinks ${(question.currentData.data.drinker.value == "Used to") ? "did" : "do"} you prefer?
			<div class="inputs">
				<div>
					<button type="submit" class="btn btn-sm btn-info">Beer</button>
					<button type="submit" class="btn btn-sm btn-info">Wine / champagne</button>
					<button type="submit" class="btn btn-sm btn-info">Hard liquor</button>
					<button type="submit" class="btn btn-sm btn-info">Cocktails</button>
					<div class="subinputs">
						<input type="text" value=""/>
						<button type="submit" class="btn btn-sm btn-info" disabled>Add</button>
					</div>
				</div>
			</div>
		</div>
		<div class="step" data-step="how_long_in_months">
			For how many years or months ${(question.currentData.data.drinker.value == "Used to") ? "did you drink" : "have you been drinking"} regularly?<br/>
			<table class="inputs">
				<tr>
					<td>Years</td>
					<td><input type="text" autofocus="autofocus" value="" name="years"/></td>
				</tr>
					<tr>
					<td>Months</td>
					<td><input type="text" value="" name="months"/></td>
				</tr>
			</table>
			<div class="table-buttons">
				<div>
					<button type="submit" class="btn btn-sm btn-info">Next</button>
				</div>
			</div>
		</div>
		<div class="step" data-step="when_stopped">
			If you stopped drinking, when did you stop?<br/>
			<input type="month" autofocus="autofocus" value="" name="stopped"/>
			<div class="table-buttons">
				<div>
					<button type="submit" class="btn btn-sm btn-info">Next</button>
				</div>
			</div>
		</div>
		<div class="step" data-step="other">
			Additional information about drinking habits (e.g. stopped, restarted)?<br/>
			<input type="text" autofocus="autofocus" value="" name="other"/>
			<div class="table-buttons">
				<div>
					<button type="submit" class="btn btn-sm btn-info">Done</button>
					<button type="submit" class="btn btn-sm btn-info">Nothing to add</button>
				</div>
			</div>
		</div>
	</form>
</div>
<div id="error"></div>
<div class="answers"></div>
