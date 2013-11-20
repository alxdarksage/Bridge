(function() {
    // Won't work on IE7. Kill me now.
    
    var tables = document.querySelectorAll(".table-selectable");
    for (var i=0; i < tables.length; i++) {
        initTable(tables[i]);
    }
    function makeRowListener(masterControl, buttons, inputs, input) {
        return function(e) {
            if(e.target.nodeName.toLowerCase() == "td") {
                input.checked = !input.checked;    
                masterControl.checked = allChecked(inputs);
                btnState(buttons, anyChecked(inputs));
            }
        }
    }
    function allChecked(inputs) {
        for (var j=0; j < inputs.length; j++) {
            if (!inputs[j].checked) {
                return false;
            }
        }
        return true;
    }
    function anyChecked(inputs) {
        for (var j=0; j < inputs.length; j++) {
            if (inputs[j].checked) {
                return true;
            }
        }
        return false;
    }
    function btnState(buttons, state) {
        for (var i=0; i < buttons.length; i++) {
            var button = buttons[i];
            if (state) {
                button.className = button.className.replace("disabled","");
            } else {
                button.className += " disabled";
            }
        }
    }
    function initTable(table) {
        var rows = table.tBodies[0].rows,
            buttons = table.querySelectorAll("tfoot button"),
            inputs = table.querySelectorAll("tbody input[type=checkbox]"),
            masterControl = table.querySelector("tfoot input[type=checkbox]");
        for (var j=0; j < rows.length; j++) {
            rows[j].addEventListener("click", makeRowListener(masterControl, buttons, inputs, inputs[j]), false);
        }
        for (var j=0; j < inputs.length; j++) {
            var input = inputs[j];
            input.addEventListener("click", function(e) {
                e.stopPropagation();
                masterControl.checked = allChecked(inputs);
                btnState(buttons, anyChecked(inputs));
            }, false);
        }
        masterControl.addEventListener("click", function(e) {
            btnState(buttons, e.target.checked);
            for (var j=0; j < inputs.length; j++) {
                var input = inputs[j];
                input.checked = e.target.checked;
            }
        }, false);
    }
})();