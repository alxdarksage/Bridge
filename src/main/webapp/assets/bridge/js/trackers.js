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
        var dataType = e.target.getAttribute("data-type"),
            value = String.fromCharCode(keyCode);
        // You have to allow for tab and shift tab, arrow keys
        if (keyCode < 48 || keyCode > 90) {
        	return;
        }
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
