(function() {
    $("#removeDefaultsAct").on('click', function(e) {
		$("#dynamicForm .defaulted").each(function() {
			$(this).val('').removeClass('defaulted');
		});
    });
    
    // With the inclusion of number fields, the arrow keys adjust the numbers,
    // and this doesn't work anymore. Currently abandoning.

    var LONG_REGEX = /\d/,
        fields = $("#dynamicForm input, #dynamicForm select");
    
    if (fields.size() === 0) {
        return;
    }
    
    fields.get(0).focus();
    
    $(document.documentElement).on('keydown', function(e) {
        if (e.target.nodeName !== "INPUT") {
            return;
        }
        var keyCode = e.keyCode;
        // Allow for ctrl-a/z/x/c/v
        if ([65,67,86,88,90].indexOf(keyCode) > -1 && (e.metaKey || e.ctrlKey)) {
        	return;
        }
        // Allow for tab and shift tab, arrow keys, backspace, etc
        if ([8, 9, 16, 17, 18, 37, 38, 39, 40, 13, 27, 91, 93].indexOf(keyCode) > -1) {
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
