function updateTimes() {
	var elements = document.getElementsByClassName("dateColumn");
	
	for(var i=0; i<elements.length; i++) {
	    var content = elements[i].innerHTML;
	    
	    var values = content.split(':');
	    
	    var seconds = parseInt(values[2]);
	    var minutes = parseInt(values[1]);
	    var hours = parseInt(values[0]);
	    
	    seconds++;
	    
	    if (seconds >= 60) {
	    	minutes++;
	    	seconds = 0;
	    }
	    
	    if (minutes >= 60) {
	    	minutes = 0;
	    	hours++;
	    }
	    
	    if (seconds < 10) {
	    	seconds = "0"+seconds;
	    }
	    
	    if (minutes < 10) {
	    	minutes = "0"+minutes;
	    }
	    
	    if (hours < 10) {
	    	hours = "0"+hours;
	    }
	    
	    elements[i].innerHTML = hours+":"+minutes+":"+seconds;
	}
}

function updateServerTimes() {
	var elements = document.getElementsByClassName("dateColumn");
	
	for(var i=0; i<elements.length; i++) {
	    var content = elements[i].getElementsByTagName("span")[0].innerHTML;
	    
	    var values = content.split(':');
	    
	    var seconds = parseInt(values[2]);
	    var minutes = parseInt(values[1]);
	    var hours = parseInt(values[0]);
	    
	    seconds++;
	    
	    if (seconds >= 60) {
	    	minutes++;
	    	seconds = 0;
	    }
	    
	    if (minutes >= 60) {
	    	minutes = 0;
	    	hours++;
	    }
	    
	    if (seconds < 10) {
	    	seconds = "0"+seconds;
	    }
	    
	    if (minutes < 10) {
	    	minutes = "0"+minutes;
	    }
	    
	    if (hours < 10) {
	    	hours = "0"+hours;
	    }
	    
	    elements[i].getElementsByTagName("span")[0].innerHTML = hours+":"+minutes+":"+seconds;
	}
}