var snapper = new Snap({
	element: document.getElementById('content'), 
	disable: "right", 
	touchToDrag: true, 
	hyperextensible: true
});
var openLeft = document.getElementById('open-left');
if (openLeft) {
	openLeft.addEventListener('click', function(e) {
		if( snapper.state().state=="left" ){
		    snapper.close();
		} else {
		    snapper.open('left');
		}
	}, false);
}

// Highlight the current page.
var n = document.querySelectorAll(".snap-drawer-left a");
for (var i=0; i < n.length; i++) {
	if (document.location.href.indexOf(n[i].getAttribute('href')) > -1) {
		$(n[i]).closest('li').addClass('active');
	}
}
