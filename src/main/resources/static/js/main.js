var searchButton = document.getElementById("searchButton");
var recordButton = document.getElementById("recordButton");
var downloadButton = document.getElementById("downloadButton");
var searchInput = document.getElementById("searchInput");
var embedUrlText = document.getElementById("embedUrl");
var nextResultsText = document.getElementById("nextResultsText");
var restoreText = document.getElementById("Restore").innerHTML;
var restoreOrder = document.getElementById("RestoreOrder").innerHTML;
var songLyrics = document.getElementById("songLyrics");
var canvasA = document.getElementById("visualizerA");
var canvasB = document.getElementById("visualizerB");

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
var queue = [0]
var prev = 0;
function draw(v, cv) {
  if (queue.length > 20) {
    queue.shift();
  }
  queue.push(Math.abs(v-prev));
  prev = v;
  var sum = 0;
  for (var i = 0; i < queue.length; i++) {
    sum += queue[i];
  }
  var avg = sum/queue.length
  if (cv.getContext) {
    var ctx = cv.getContext('2d');
    ctx.clearRect ( 0 , 0 , 400,200);
    ctx.beginPath();
    
    //ctx.arc(100, Math.min(Math.round(100,v/200)),200000/v, Math.PI*.5 - Math.min(Math.PI*.5, v/3000),Math.PI*.5 + Math.min(Math.PI*.5, v/3000) );
    ctx.arc(185,100,Math.min(v/50,100), 0,Math.PI*2);
    var c = Math.min(255,Math.round(1.5*avg));
    
    ctx.fillStyle = "rgb("+c+","+200+","+c+")";
    ctx.fill();
    //ctx.lineWidth = 10;
    //ctx.strokeStyle = '#ff0000';
    ctx.stroke();
  }
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
 var smooth = 0
setInterval(function () {
$.post("/visualize", null, function(responseJSON) {
    var val = JSON.parse(responseJSON);
    smooth = Math.round(smooth*.9 + val.level*.1);
    console.log(val);
    //searchButton.style.width = "" + Math.round(200*Math.exp(smooth/10000)) + "px";
   // recordButton.style.width = "" + Math.round(200*Math.exp(smooth/10000)) + "px";
   // var c = Math.min(255,Math.round(smooth/50));
    //document.body.style.backgroundColor = "rgb("+125+","+c+","+c+")";
    draw(smooth);
  });}, 150);
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

function showDownloadButton() {
	downloadButton.style.display = "block";
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
	downloadUrl = currentResults.resultUrl[0];
	showDownloadButton();
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
	downloadUrl = currentResults.resultUrl[0];
	showDownloadButton();
};

var selectAll = function(id) {
	searchInput.focus();
	searchInput.select();
}

var recordRec = function() {
	$.post("/record", null, function(responseJSON) {
		console.log(responseJSON);
		response = JSON.parse(responseJSON);
		newWords = response.words;
 var smooth = 0
setInterval(function () {
$.post("/visualize", null, function(responseJSON) {
    var val = JSON.parse(responseJSON);
    smooth = Math.round(smooth*.9 + val.level*.1);
    console.log(val);
    //searchButton.style.width = "" + Math.round(200*Math.exp(smooth/10000)) + "px";
   // recordButton.style.width = "" + Math.round(200*Math.exp(smooth/10000)) + "px";
   // var c = Math.min(255,Math.round(smooth/50));
    //document.body.style.backgroundColor = "rgb("+125+","+c+","+c+")";
    draw(smooth);
  });}, 50);
		for (var i = 0; i < newWords.length; i++){
			searchInput.value = searchInput.value + newWords[i] + " ";
		}
		recordRec();
	});
};

var download = function() {
 var smooth = 0
setInterval(function () {
$.post("/visualize", null, function(responseJSON) {
    var val = JSON.parse(responseJSON);
    smooth = Math.round(smooth*.7 + val.level*.3);
    console.log(val);
    //searchButton.style.width = "" + Math.round(200*Math.exp(smooth/10000)) + "px";
   // recordButton.style.width = "" + Math.round(200*Math.exp(smooth/10000)) + "px";
   // var c = Math.min(255,Math.round(smooth/50));
    //document.body.style.backgroundColor = "rgb("+125+","+c+","+c+")";
    draw(smooth);
  });}, 50);
	var postParams = {
		"currentResults" : downloadUrl
	}
	$.post("/download", postParams, function(responseJSON) {

	});
}

var record = function() {
	searchInput.value = "";
	recordRec();
}

$("a[data-text]").click(function(){
  $("#searchInput").val($(this).attr("data-text"))
  return false;
})

 var smooth = 0
setInterval(function () {
$.post("/visualize", null, function(responseJSON) {
    var val = JSON.parse(responseJSON);
    smooth = Math.round(smooth*.9 + val.level*.1);
    console.log(val);
    //searchButton.style.width = "" + Math.round(200*Math.exp(smooth/10000)) + "px";
   // recordButton.style.width = "" + Math.round(200*Math.exp(smooth/10000)) + "px";
   // var c = Math.min(255,Math.round(smooth/50));
    //document.body.style.backgroundColor = "rgb("+125+","+c+","+c+")";
    draw(smooth,canvasA);
    draw(smooth,canvasB);    
  });}, 150);


searchInput.onclick = selectAll;

searchButton.onclick = search;
recordButton.onclick = record;
downloadButton.onclick = download;
