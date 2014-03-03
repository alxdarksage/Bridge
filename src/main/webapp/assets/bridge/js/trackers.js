(function() {
    $("#removeDefaultsAct").on('click', function(e) {
		$("#dynamicForm .defaulted").each(function() {
			$(this).val('').removeClass('defaulted');
		});
    });

    function tabThroughDefaults(e, fields) {
    	var array = $("#dynamicForm input, #dynamicForm select").toArray();
    	e.preventDefault();
    	e.stopPropagation(); // prevents html5 shim from doing stuff
    	
    	var index = array.indexOf(e.target);
    	var delta = (e.shiftKey) ? -1 : 1;
    	
    	var nextElement = array[index+delta];
    	while( !$(nextElement).is(":visible") || $(nextElement).hasClass("defaulted") ) {
    		index = index + delta;
    		index = (index > array.length-1) ? 0 : (index < 0) ? (array.length-1) : index; 
    		nextElement = array[index];
    	}
    	nextElement.focus();
    }
    
    // With the inclusion of number fields, the arrow keys adjust the numbers,
    // and this doesn't work anymore. Currently abandoning.

    var LONG_REGEX = /\d/,
    	fields = $("#dynamicForm input, #dynamicForm select");
    
    $(document.documentElement).on('keydown', function(e) {
    	if (fields.size() == 0 || (e.target.nodeName !== "INPUT" && e.target.nodeName !== "SELECT")) {
    		return;
    	}
        var keyCode = e.keyCode;
        if (keyCode === 9) {
        	return tabThroughDefaults(e, fields);
        }
        // Allow for ctrl-a/z/x/c/v
        if ([65,67,86,88,90].indexOf(keyCode) > -1 && (e.metaKey || e.ctrlKey)) {
        	return;
        }
        // Allow for tab and shift tab, arrow keys, backspace, etc
        if ([8, 16, 17, 18, 37, 38, 39, 40, 13, 27, 91, 93].indexOf(keyCode) > -1) {
        	return;
        }
        var dataType = e.target.getAttribute("data-type"),
        	value = String.fromCharCode(keyCode);
        // This does not allow for negative numbers.
        if (dataType === "double") {
            if (keyCode !== 190 && !LONG_REGEX.test(value)) {
                e.preventDefault();
                e.stopPropagation();
            }
        } else if (dataType === "long") {
            if (!LONG_REGEX.test(value)) {
                e.preventDefault();
                e.stopPropagation();
            }
        }
    });
})();
