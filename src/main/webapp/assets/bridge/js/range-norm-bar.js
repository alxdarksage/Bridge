(function() {
var bars = document.querySelectorAll(".rangeNormBar");
for (var i=0; i < bars.length; i++) {
    rangeNormBar(bars[i]);
}

function rangeNormBar(bar, value, unit, minValue, maxValue) {
    var width = 250;
    var height = 25;

    var valueEl = bar.querySelector(".value");
    if (!valueEl) {
    	return; // empty
    }
    var range = bar.querySelector(".range");
    if (range) {
        // Normalize max/min values to 25%/75% of bar.
    	var value = parseFloat(valueEl.textContent);
        var minValue = parseFloat(range.getAttribute("data-lower"));
        var maxValue = parseFloat(range.getAttribute("data-upper"));
        var canvas = bar.querySelector("canvas");
        var context = canvas.getContext("2d");
        context.width = width;
        context.height = height;
        context.fillStyle = "rgb(240,240,240)";
        context.fillRect(75,0,150,150);
        
        var adjValue = 25 + (value-minValue)*(50)/(maxValue-minValue);
        adjValue = ((adjValue)*3) - 2; // converting to pixels.
        if (adjValue < 0) adjValue = 0;
        if (adjValue > 296) adjValue = 296;
        
        var color = (value < minValue || value > maxValue) ? "rgb(800,0,0)" : "rgb(0,200,0)";
        context.fillStyle = color;
        context.fillRect (adjValue, 0, 4, 150);
    }
}
})();