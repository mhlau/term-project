var searchButton = document.getElementById("searchButton");
var recordButton = document.getElementById("recordButton");
var searchInput = document.getElementById("searchInput");
var embedUrlText = document.getElementById("embedUrl");
var nextResultsText = document.getElementById("nextResultsText");
var restoreText = document.getElementById("Restore").innerHTML;
var restoreOrder = document.getElementById("RestoreOrder").innerHTML;
var songLyrics = document.getElementById("songLyrics");
var currentResults;
var currentOrder = [0,1,2,3,4];
var downloadUrl;

function swapCurrRes(i,j) {
	tempU = currentResults.resultUrl[i];
  tempT = currentResults.resultTitle[i];
	tempL = currentResults.resultLyrics[i];
	
	currentResults.resultUrl[i] = currentResults.resultUrl[j];
  currentResults.resultTitle[i] = currentResults.resultTitle[j];
  currentResults.resultLyrics[i] = currentResults.resultLyrics[j];
    
  currentResults.resultUrl[j] = tempU;
  currentResults.resultTitle[j] = tempT;
  currentResults.resultLyrics[j] = tempL;
}
function changeOrder(newOrder) {
  for (var i = 0; i < 5; i++) {
    swapCurrRes(i, currentOrder.indexOf(newOrder[i]));
    currentOrder[currentOrder.indexOf(newOrder[i])] = currentOrder[i]
    currentOrder[i] = newOrder[i]
  }
}

function changeSelected(i) {
  var newArr = [currentOrder[i]];
  for (var j = 1; j < 5; j++) {
    if (j == i) {
      newArr[j] = currentOrder[0];
    } else {
      newArr[j] = currentOrder[j];
    }
  }
  console.log(newArr);
  changeOrder(newArr);
  console.log(currentOrder);
  reloadEmbed();
  history.pushState(null,null,"/"+currentResults.saveId + "" + packOrdering(currentOrder));

}

//~ function unpackOrdering(num) {
  //~ var start = [0,1,2,3,4];
  //~ var retarr = [num%5];
  //~ start.splice(num%5,1);
  //~ num = num/5;
  //~ for (var j = 1; j < 5; j++) {
    //~ var guess = num%(5-j);
    //~ num = num/(5-j);
    //~ retarr[j] = start[guess];
    //~ start.splice(guess,1);
  //~ }
  //~ return retarr;
//~ }

function unpackOrdering(num) {
  var def = [0,1, 2, 3, 4];
  var ret = []
  num = parseInt(num) - 40000;
  if (num < 0) {
    return def;
  }
  for (var i = 0; i < 5; i++) {
     console.log(i);
     console.log(ret);
     console.log(num);
    ret[i] = num%10;
    if (ret[i] > 4 || ret.indexOf(ret[i]) != i) {
      return def;
    }
    
    num = Math.floor(num/10);
  }
  return ret;
}

function packOrdering(order) {

   return order[0] + 10*order[1] +100*order[2]+1000*order[3]+10000*order[4] + 40000;
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
	songLyrics.innerHTML = currentResults.resultLyrics[0];
}

if (!(restoreText==="")) {
  console.log(restoreText);
  currentResults = JSON.parse(restoreText);
  changeOrder(unpackOrdering(JSON.parse(restoreOrder)));
  reloadEmbed();
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
    history.pushState(null,null,"/"+currentResults.saveId + "" + packOrdering(currentOrder));
	});
};

var selectAll = function(id) {
	searchInput.focus();
	searchInput.select();
}

var record = function() {
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

searchInput.onclick = selectAll;
searchButton.onclick = search;
recordButton.onclick = record;
