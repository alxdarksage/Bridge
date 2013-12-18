(function() {

	function ajax(url, callback) {
        this.postBody = (arguments[2] || "");
        this.callback = callback;
        this.url = url;
        var req = this.request = new XMLHttpRequest();
        
        if (this.postBody) {
            req.open("POST", url, true);
            req.responseType = "document";
            req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            req.onload = callback;            
        } else {
            req.open("GET", url, true);
            req.responseType = "document";
            req.onload = callback;
        }
        req.send(this.postBody);
	}	

	function visit(element, func) {
		if (func(element)) {
			for (var i=0, len = element.childNodes.length; i < len; i++) {
				visit(element.childNodes[i], func);
			}
		}
	}
	
	function walk(start, axis, property, value) {
		while(start && start[property] != value) {
			start = start[axis];
		}
		return start;
	}
	
	function hasSameChildren(dest, update) {
		if (dest == null || update == null) {
			return false;
		}
		if (dest.hasChildNodes() && update.hasChildNodes()) {
			if (dest.childNodes.length != update.childNodes.length) {
				return false;
			}
			for (var i=0, len = dest.childNodes.length; i < len; i++) {
				var n1 = dest.childNodes[i], n2 = update.childNodes[i];
				if ((n1.nodeName !== n2.nodeName) ||
			        (n1.nodeType !== n2.nodeType) ||
			        (n1.nodeValue !== n2.nodeValue)) {
					return false;
				}
				if (n1.nodeType === 1) {
					var atts1 = n1.attributes, atts2 = n2.attributes;
					if (atts1.length != atts2.length) {
						return false;
					}
					for (var prop in atts1) {
						if (atts1[prop].value !== atts2[prop].value) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	function compare(dest, update) {
		if (hasSameChildren(dest, update)) {
			for (var i=0; i < dest.childNodes.length; i++) {
				compare(dest.childNodes[i], update.childNodes[i]);
			}
		} else {
			if (dest && !update) {
				console.log("Removing node: ", dest);
				dest.parentNode.removeChild(dest);
			} else if (!dest && update) {
				alert("This happens");
				// pffft. May need to iterate over longest 
			} else {
				console.log("Replacing node: ", dest);
				dest.parentNode.replaceChild(update, dest);
			}
			window.scrollTo(0,1);
		}
	}
	
	function formToQuery(form) {
		var array = [];
		for (var i=0; i < form.elements.length; i++) {
			var field = form.elements[i];
			if (field.name !== "") {
				array.push( encodeURIComponent(field.name) + "=" + encodeURIComponent(field.value));
			}
		}
		return array.join("&");
	}
	
	function handleElement(e) {
		var link = walk(e.target, "parentNode", "nodeName", "A");
		if (link) {
			var url = link.getAttribute('href');
			if (url) {
				e.preventDefault();
				ajax(url, handleResponse);
			}
		}
	}
	
	function handleForm(e) {
		var enctype = e.target.getAttribute('enctype');
		if (enctype !== "multipart/form-data") {
			e.preventDefault();	
			var url = e.target.getAttribute('action');
			var query = formToQuery(e.target);
			console.log("Form submitted", query);
			ajax(url, handleResponse, query);
		}
		// otherwise let it run as normal
	}

	function handleResponse() {
		var finalUrl = this.getResponseHeader('X-Bridge-Origin');
		document.title = this.responseXML.title + " (AJAX)";
		history.pushState(null, null, finalUrl);
		var current = document.body.querySelector(".container");
		var update = this.responseXML.querySelector(".container");
		/*
		var current = document.body;
		var update = this.responseXML.body;
		*/
		compare(current, update);
	}
	
	window.addEventListener("load", function() {
		document.documentElement.addEventListener("click", handleElement, true);
		document.documentElement.addEventListener("submit", handleForm, true);
	}, false);
})();

