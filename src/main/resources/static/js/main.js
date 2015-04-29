var searchButton = document.getElementById("searchButton");
var recordButton = document.getElementById("recordButton");
var searchInput = document.getElementById("searchInput");
var resultUrlText = document.getElementById("resultUrl");

var search = function() {
	var searchVal = searchInput.value;
	var postParams = {
		"searchVal" : searchVal
	};
	$.post("/result", postParams, function(responseJSON) {
		response = JSON.parse(responseJSON);
		resultUrl = response.resultUrl;
		resultUrlText.innerHTML = 
			"<iframe width=\"560\" height=\"315\"src=\"" 
			+ resultUrl.resultUrl 
			+ "?autoplay=1\"frameborder=\"0\" allowfullscreen></iframe>";
		console.log(resultUrl);		
	});
};

var record = function() {
	$.post("/record", null, function(responseJSON) {
		response = JSON.parse(responseJSON);
		console.log(response.word)
	});
};

searchButton.onclick = search;
recordButton.onclick = record;
