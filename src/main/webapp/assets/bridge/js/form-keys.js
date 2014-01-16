(function() {
    $("#removeDefaultsAct").on('click', function(e) {
		$("#dynamicForm .defaulted").each(function() {
			$(this).val('').removeClass('defaulted');
		});
    });
    
    // With the inclusion of number fields, the arrow keys adjust the numbers,
    // and this doesn't work anymore. Currently abandoning.

    var COLUMNS = 4,
        LONG_REGEX = /\d/,
        METAS = [8, 9],
        fields = $("#dynamicForm input, #dynamicForm select");
    
    if (fields.size() === 0) {
        return;
    }
    
    fields.get(0).focus();
    
    /*
    function move(element, delta) {
        var index = fields.indexOf(element);
        if (inBounds(index+delta)) {
            var newField = fields.eq(index+delta).focus()[0];
            if (newField.select) {
                newField.select();  
            }
            if (newField.type === "hidden") {
                move(newField, delta);
            }
        }
    }
    function inBounds(index) {
        return index >= 0 && index < fields.size();
    }
    */
    
    $(document.documentElement).on('keydown', function(e) {
        if (e.target.nodeName !== "INPUT") {
            return;
        }
        var keyCode = e.keyCode;
        /*
        if (METAS.indexOf(keyCode) > -1 || e.ctrlKey || e.shiftKey || e.metaKey || e.altKey) {
            return;
        }
        if (keyCode === 37) {
            move(e.target, -1); // left
        } else if (keyCode === 38) {
            move(e.target, -COLUMNS); // up
        } else if (keyCode === 39) {
            move(e.target, 1); // right
        } else if (keyCode === 40) {
            move(e.target, COLUMNS); // down
        }
        */
        var dataType = e.target.getAttribute("data-type"),
            value = String.fromCharCode(keyCode);
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
