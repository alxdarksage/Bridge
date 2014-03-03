<%@tag import="org.eclipse.jetty.util.ajax.JSON"%>
<%@ include file="../../jsp/directives.jsp" %>
<%@ attribute name="series" required="true" type="java.lang.String" %>
<%@ attribute name="timelineStart" required="false" type="java.lang.Long" %>
<%@ attribute name="columns" required="false" type="String" %>
<%
	String id = "timeseries-" + series + "-" + columns;
%>
<div id="<%= id %>"></div>
<script type="text/javascript" src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization',
       'version':'1','packages':['corechart']}]}"></script>
<script type="text/javascript">
	google.setOnLoadCallback( function () {
		var url = "/bridge/journal/${sessionScope.BridgeUser.ownerId}/series/${series}/ajax/timeseries.html";
		$.ajax({
			success: function(data) {
				drawTable(data, "<%= id %>");
			},
			error: function(req,error,status) {
				$("#<%= id %>").text(status +": " + error);
			},
			type: "GET",
			url: url,
			dataType: "json",
			data: {columns: "${columns}"}
		});
		var timelineStart = ${not empty timelineStart ? timelineStart : null};

		var eventSeriesOptions = {
			targetAxisIndex: 1,
			visibleInLegend: false,
			lineWidth: 4,
			color: "#888",
			annotations: {
			    boxStyle: {
			        stroke: '#888',           // Color of the box outline.
			        strokeWidth: 1,           // Thickness of the box outline.
			    }
			}
		};

		function drawTable(data, id) {
	        var dataTable = new google.visualization.DataTable();
	        var hasEvents = data.events != null && data.events.length > 0;
	        var series = [];
			var vAxes = [{}];
			var height = -1;

	        for(var i = 0; i < data.cols.length; i++) {
	        	dataTable.addColumn(data.cols[i].type, data.cols[i].name);
	        }
	        if ( hasEvents ) {
	        	dataTable.addColumn( "number", "medication");
	        	dataTable.addColumn({type: "string", role: "annotation"});
	        	series[data.cols.length - 1] = eventSeriesOptions;
	        	vAxes[1] = {
	    	        maxValue: data.events.length,
	    	        gridlines: {
	    				color: "white"
	    			},
	    			textPosition: "none"
    			}
	        }

        	for(var i = 0; i < data.rows.length; i++) {
	        	data.rows[i][0] = new Date(parseInt(data.rows[i][0]));
	            for(var j = 1; j < data.rows[i].length; j++) {
	            	data.rows[i][j] = data.rows[i][j] == null ? null : parseFloat(data.rows[i][j]);
	            }
	            if ( hasEvents ) {
		            data.rows[i].push(null);
		            data.rows[i].push(null);
	            }
	            dataTable.addRow(data.rows[i]);
	        }

        	if ( hasEvents ) {
		        for(var i = 0; i < data.events.length; i++) {
		        	row = new Array(data.cols.length);
		        	row[0] = new Date(parseInt(data.events[i].start));
		        	row.push(i + 1);
		        	row.push(data.events[i].name);
		            dataTable.addRow(row);

		            row = new Array(data.cols.length);
		        	row[0] = data.events[i].end == null ? new Date() : new Date(parseInt(data.events[i].end));
		        	row.push(i + 1);
		        	row.push(null);
		            dataTable.addRow(row);

		            row = new Array(data.cols.length);
		        	row[0] = data.events[i].end == null ? new Date() : new Date(parseInt(data.events[i].end));
		        	row.push(null);
		        	row.push(null);
		            dataTable.addRow(row);
	        	}
	        	height = data.events.length * 50 + 50;
        	}

		    var options = {
				height: height,
		    	title: '',
	    	 	vAxes: vAxes,
	    	 	hAxis: {
		    	 	maxValue: new Date()
	    	 	},
            	series: series,
            	annotations: {
            		boxStyle: {
            			stroke: '#888',
            		    strokeWidth: 1
            		}
            	},
            	chartArea: {
                	top: 10,
                	left: 40,
                	width:"80%"
                }
		    };
			if ( hasEvents ) {
				
			}

			if ( timelineStart != null ) {
			    options['hAxis']['minValue'] = new Date(timelineStart);
			}

		    var chart = new google.visualization.LineChart(document.getElementById(id));
		    chart.draw(dataTable, options);
		}
	});
</script>
