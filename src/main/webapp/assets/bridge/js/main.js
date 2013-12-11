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
