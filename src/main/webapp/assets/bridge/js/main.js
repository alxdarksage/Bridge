// Empty.

var photoUpload = document.getElementById('photoFileInput');
if (photoUpload) {
	// Ah IE.
	photoUpload.addEventListener('change', function(e) {
		var URL = window.URL || window.webkitURL;
		document.getElementById('photoImg').src = URL.createObjectURL(e.target.files[0]);
	}, false);
}