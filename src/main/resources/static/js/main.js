var searchButton = document.getElementById("searchButton");
var recordButton = document.getElementById("recordButton");
var stopButton = document.getElementById("stopButton");
var searchInput = document.getElementById("searchInput");
var embedUrlText = document.getElementById("embedUrl");
var nextResultsText = document.getElementById("nextResultsText")
var currentResults;
var downloadUrl;
var listening = false;

function changeSelected(i) {
    tempT = currentResults.resultTitle0;
	tempU = currentResults.resultUrl0;
	if (i == 1) {
    		currentResults.resultUrl0 = currentResults.resultUrl1;
    		currentResults.resultTitle0 = currentResults.resultTitle1;
    		currentResults.resultUrl1 = tempU;
    		currentResults.resultTitle1 = tempT;
  	} else if (i == 2) {
    		currentResults.resultUrl0 = currentResults.resultUrl2;
    		currentResults.resultTitle0 = currentResults.resultTitle2;
    		currentResults.resultUrl2 = tempU;
    		currentResults.resultTitle2 = tempT;
	} else if (i == 3) {
    		currentResults.resultUrl0 = currentResults.resultUrl3;
    		currentResults.resultTitle0 = currentResults.resultTitle3;
    		currentResults.resultUrl3 = tempU;
    		currentResults.resultTitle3 = tempT;
	} else if (i == 4) {
    		currentResults.resultUrl0 = currentResults.resultUrl4;
    		currentResults.resultTitle0 = currentResults.resultTitle4;
    		currentResults.resultUrl4 = tempU;
    		currentResults.resultTitle4 = tempT;
	}
	embedUrl = currentResults.resultUrl0;
	downloadUrl = currentResults.resultUrl0;
	embedUrlText.innerHTML = 
		"<iframe width=\"560\" height=\"315\"src=\"" 
		+ embedUrl 
		+ "?autoplay=1\"frameborder=\"0\" allowfullscreen></iframe>";
	nextResultsText.innerHTML = 
		"Wasn't relevant? Try these songs: <br>"
		+ "<a href=\"javascript:void(0)\" onclick=\"changeSelected(1);\">" + currentResults.resultTitle1 + "</a><br>"
		+ "<a href=\"javascript:void(0)\" onclick=\"changeSelected(2);\">" + currentResults.resultTitle2 + "</a><br>"
		+ "<a href=\"javascript:void(0)\" onclick=\"changeSelected(3);\">" + currentResults.resultTitle3 + "</a><br>"
		+ "<a href=\"javascript:void(0)\" onclick=\"changeSelected(4);\">" + currentResults.resultTitle4 + "</a>"
}

function showDownloadButton() {
	downloadButton.style.display = "block";
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
		embedUrl = currentResults.resultUrl0;
		showDownloadButton();
		downloadUrl = currentResults.resultUrl0;
		embedUrlText.innerHTML = 
			"<iframe width=\"560\" height=\"315\"src=\"" 
			+ embedUrl 
			+ "?autoplay=1\"frameborder=\"0\" allowfullscreen></iframe>";
		nextResultsText.innerHTML = 
			"Wasn't relevant? Try these songs: <br>"
			+ "<a href=\"javascript:void(0)\" onclick=\"changeSelected(1);\">" + response.result.resultTitle1 + "</a><br>"
			+ "<a href=\"javascript:void(0)\" onclick=\"changeSelected(2);\">" + response.result.resultTitle2 + "</a><br>"
			+ "<a href=\"javascript:void(0)\" onclick=\"changeSelected(3);\">" + response.result.resultTitle3 + "</a><br>"
			+ "<a href=\"javascript:void(0)\" onclick=\"changeSelected(4);\">" + response.result.resultTitle4 + "</a>"
	});
};

var record = function() {
	searchInput.value = "";
	listening = true;
};

var stopListening = function() {
	listening = false;
}

var download = function() {
	var postParams = {
		"currentResults" : downloadUrl
	}
	$.post("/download", postParams, function(responseJSON) {

	});
}

$("a[data-text]").click(function(){
  $("#searchInput").val($(this).attr("data-text"))
  return false;
})

searchButton.onclick = search;
recordButton.onclick = record;
downloadButton.onclick = download;
stopButton.onclick = stopListening;
