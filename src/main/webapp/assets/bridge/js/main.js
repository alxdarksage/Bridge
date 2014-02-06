// Empty.

var photoUpload = document.getElementById('photoFileInput');
if (photoUpload) {
	// Ah IE.
	photoUpload.addEventListener('change', function(e) {
		var URL = window.URL || window.webkitURL;
		document.getElementById('photoImg').src = URL.createObjectURL(e.target.files[0]);
	}, false);
}

var deletes = document.querySelectorAll("[data-confirm]");
for (var i=0; i < deletes.length; i++) {
	var del = deletes[i];
	del.addEventListener("click", function(e) {
    	if (!confirm(del.getAttribute('data-confirm'))) {
    		e.preventDefault();
    	}
    }, false);
}

function throttle(func, wait, options) {
	var context, args, result;
	var timeout = null;
	var previous = 0;
	options || (options = {});
	var later = function() {
		previous = options.leading === false ? 0 : new Date;
		timeout = null;
		result = func.apply(context, args);
	};
	return function() {
		var now = new Date;
		if (!previous && options.leading === false)
			previous = now;
		var remaining = wait - (now - previous);
		context = this;
		args = arguments;
		if (remaining <= 0) {
			clearTimeout(timeout);
			timeout = null;
			previous = now;
			result = func.apply(context, args);
		} else if (!timeout && options.trailing !== false) {
			timeout = setTimeout(later, remaining);
		}
		return result;
	};
};