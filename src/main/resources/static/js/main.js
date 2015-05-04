var searchButton = document.getElementById("searchButton");
var recordButton = document.getElementById("recordButton");
var searchInput = document.getElementById("searchInput");
var embedUrlText = document.getElementById("embedUrl");
var nextResultsText = document.getElementById("nextResultsText")
var currentResults;
function changeSelected(i) {
        tempT = currentResults.resultTitle[0];
	tempU = currentResults.resultUrl[0];
	currentResults.resultUrl[0] = currentResults.resultUrl[i];
    	currentResults.resultTitle[0] = currentResults.resultTitle[i];
    	currentResults.resultUrl[i] = tempU;
    	currentResults.resultTitle[i] = tempT;
	reloadEmbed();

}

var reloadEmbed = function() {
	embedUrl = currentResults.resultUrl[0];
	embedUrlText.innerHTML = 
		"<iframe width=\"560\" height=\"315\"src=\"" 
		+ embedUrl 
		+ "?autoplay=1\"frameborder=\"0\" allowfullscreen></iframe>";
	nextResultsText.innerHTML =  "Wasn't relevant? Try these songs: <br>";
	for (var i = 1; i < 5; i++) {
		nextResultsText.innerHTML =  nextResultsText.innerHTML + 
			"<a href=\"javascript:void(0)\" onclick=\"changeSelected(" + i
                                + ");\">" + currentResults.resultTitle[i] + "</a><br>"
	}
}
var search = function() {
	var searchVal = searchInput.value;
	var postParams = {
		"searchVal" : searchVal
	};
	$.post("/result", postParams, function(responseJSON) {
		response = JSON.parse(responseJSON);
		console.log(response);	
		currentResults = response.result;
		reloadEmbed();
	});
};

var record = function() {;
	$.post("/record", null, function(responseJSON) {
		console.log(responseJSON);
		response = JSON.parse(responseJSON);
		newWords = response.words;
		for (var i = 0; i < newWords.length; i++){
			searchInput.value = searchInput.value + newWords[i] + " ";
		}
		record();
	});
};

$("a[data-text]").click(function(){
  $("#searchInput").val($(this).attr("data-text"))
  return false;
})

searchButton.onclick = search;
recordButton.onclick = record;
