
var url = "http://127.0.0.1:3000/"

function agregar(){
	var name = $("#name")[0].value;
	var cat = $("#category")[0].value;
	var link = $("#url")[0].value;
	var page = $("#page")[0].value;
	var lang = $("#language")[0].value;
	
	if (name != "" && cat != "" && link != "" && page != "" && lang != ""){
		var feed = {'url': link, 'urlpagina': page, 'nombre': name, 'categoria': cat, 'idioma': lang};
		guardar(feed);
	}
	else{
		alert("Información incompleta");
	}
	
}


//Guardar link en DB
function guardar(feed){
	var v_url = url+"guardar/";
	console.log(v_url);	
	var xhr = createCORSRequest('POST', v_url);
	if (!xhr) {
		throw new Error('CORS not supported');
	}
	
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.onload = function() {
		var responseText = xhr.responseText;
		alert(responseText);
	};

	xhr.onerror = function() {
		console.log('There was an error!');
	};
	
	xhr.send(JSON.stringify(feed));
	
}

function createCORSRequest(method, url) {
	var xhr = new XMLHttpRequest();
	
	if ("withCredentials" in xhr) {

		// Check if the XMLHttpRequest object has a "withCredentials" property.
		// "withCredentials" only exists on XMLHTTPRequest2 objects.
		xhr.open(method, url, true);

	} else if (typeof XDomainRequest != "undefined") {

		// Otherwise, check if XDomainRequest.
		// XDomainRequest only exists in IE, and is IE's way of making CORS requests.
		xhr = new XDomainRequest();
		xhr.open(method, url);

	} else {

		// Otherwise, CORS is not supported by the browser.
		xhr = null;

	}
	return xhr;
}