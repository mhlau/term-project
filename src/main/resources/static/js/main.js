var searchButton = document.getElementById("searchButton");
var recordButton = document.getElementById("recordButton");
var searchInput = document.getElementById("searchInput");
var embedUrlText = document.getElementById("embedUrl");
var nextResultsText = document.getElementById("nextResultsText")

var search = function() {
	var searchVal = searchInput.value;
	var postParams = {
		"searchVal" : searchVal
	};
	$.post("/result", postParams, function(responseJSON) {
		response = JSON.parse(responseJSON);
		console.log(response);	
		embedUrl = response.result.embedUrl;
		embedUrlText.innerHTML = 
			"<iframe width=\"560\" height=\"315\"src=\"" 
			+ embedUrl 
			+ "?autoplay=1\"frameborder=\"0\" allowfullscreen></iframe>";
		nextResultsText.innerHTML = 
			"Wasn't relevant? Try these songs: <br>"
			+ "<a href=\"" + response.result.resultUrl1 + "\">" + response.result.resultTitle1 + "</a><br>"
			+ "<a href=\"" + response.result.resultUrl2 + "\">" + response.result.resultTitle2 + "</a><br>"
			+ "<a href=\"" + response.result.resultUrl3 + "\">" + response.result.resultTitle3 + "</a><br>"
			+ "<a href=\"" + response.result.resultUrl4 + "\">" + response.result.resultTitle4 + "</a>"; 
	});
};

var record = function() {
	$.post("/record", null, function(responseJSON) {
		response = JSON.parse(responseJSON);
		console.log(response.word)
	});
};

$("a[data-text]").click(function(){
  $("#searchInput").val($(this).attr("data-text"))
  return false;
})

searchButton.onclick = search;
recordButton.onclick = record;
