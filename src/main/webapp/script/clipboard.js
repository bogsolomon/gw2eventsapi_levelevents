var clips = new Array();

function createClipboards() {
	var elements = document.getElementsByClassName("clipboard");
	
	for(var i=0; i<elements.length; i++) {
		var clip = new ZeroClipboard(elements[i].getElementsByTagName("button")[0], {
			  moviePath: "script/ZeroClipboard.swf"
			});
		
		clips[elements[i].id] = clip;
	}
}

function updatePositions() {
	var elements = document.getElementsByClassName("clipboard");
	
	for(var i=0; i<elements.length; i++) {
		var clip = new ZeroClipboard(elements[i].getElementsByTagName("button")[0], {
			  moviePath: "script/ZeroClipboard.swf"
			});
	}
}