<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="series" required="true" type="java.lang.String" %>
<div id="timeseries-${series}"></div>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">
	google.load("visualization", "1", {packages:["corechart"]});
	google.setOnLoadCallback( function () {
		var url = "/bridge/journal/${sessionScope.BridgeUser.ownerId}/series/${series}/ajax/timeseries.html";
		$.ajax({
			success: function(data) {
				drawChart(data);
			},
			error: function(req,error,status) {
				$("#timeseries-${series}").text(status +": " + error);
			},
			type: "GET",
			url: url,
			dataType: "json"
		});
	});

    function drawChart(data) {
        var dataTable = new google.visualization.DataTable();
        for(var i = 0; i < data.cols.length; i++) {
        	dataTable.addColumn(data.cols[i].type, data.cols[i].name);
        }
        for(var i = 0; i < data.rows.length; i++) {
        	data.rows[i][0] = new Date(parseInt(data.rows[i][0]));
            for(var j = 1; j < data.rows[i].length; j++) {
            	data.rows[i][j] = data.rows[i][j] == null ? null : parseFloat(data.rows[i][j]);
            }
            dataTable.addRow(data.rows[i]);
        }

	    var options = {
	      title: ''
	    };

	    var chart = new google.visualization.LineChart(document.getElementById("timeseries-${series}"));
	    chart.draw(dataTable, options);
	}
</script>
