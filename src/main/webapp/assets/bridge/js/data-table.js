(function() {
    function DataTable(target) {
    	var $target = $(target);
    	var $table = $target.closest('.table-selectable');
    	this.target = $target.get(0);
		this.buttons = $table.find("tfoot button").get();
		this.inputs = $table.find("tbody input[type=checkbox]").get();
		this.masterControl = $table.find("tfoot input[type=checkbox]").get(0);
		this.input = $target.closest('tr').find("input[type=checkbox]").get(0);
    }
    DataTable.prototype = {
	    updateCheckState: function() {
	    	this.masterControl.checked = this.allChecked();
	        this.btnState(this.anyChecked());    	
	    },
	    toggle: function() {
	    	if (!this.input.disabled) {
	    		this.input.checked = !this.input.checked;	
	    	}
	    },
	    toggleAll: function() {
	    	var checked = this.target.checked;
	        this.btnState(checked);
	        for (var i=0; i < this.inputs.length; i++) {
	        	if (!this.inputs[i].disabled) {
	        		this.inputs[i].checked = checked;
	        	}
	        }
	    },
	    allChecked: function() {
	        for (var j=0; j < this.inputs.length; j++) {
	            if (!this.inputs[j].checked) {
	                return false;
	            }
	        }
	        return true;
	    },
	    anyChecked: function() {
	        for (var j=0; j < this.inputs.length; j++) {
	            if (this.inputs.checked) {
	                return true;
	            }
	        }
	        return false;
	    },
	    btnState: function(state) {
	        for (var i=0; i < this.buttons.length; i++) {
	            var button = this.buttons[i];
	            if (state) {
	            	$(button).removeClass("disabled");
	            } else {
	            	$(button).addClass("disabled");
	            }
	        }
	    }	    
    };
    
    $(function() {
    	$(document.documentElement).on('click', '.table-selectable tbody td', function(e) {
    		if (e.target.nodeName !== "INPUT" && e.target.nodeName !== "A") {
        		var dt = new DataTable(e.target);
        		dt.toggle();
        		dt.updateCheckState();
    		}
    	}).on('click', '.table-selectable tbody input[type=checkbox]', function(e) {
    		var dt = new DataTable(e.target);
    		dt.updateCheckState();
    	}).on('click', '.table-selectable tfoot input[type=checkbox]', function(e) {
    		e.stopPropagation();
    		var dt = new DataTable(e.target);
    		dt.toggleAll();
    	});
    });
})();