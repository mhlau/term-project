<#assign content>

<h1 class="title" id="titleText">Speech-to-Text Music Finder</h1>
<p hidden id="Restore">${oldResults}</p>
<p hidden id="RestoreOrder">${resultsOrdering}</p>
<p hidden id="Results">${result}</p>
<div class="wrapper">
  <div id="resultVideoDiv">
    <p id="embedUrl"></p>
    <br>
    <p id="nextResultsText"></p>
    <br>
    <div class="scroll-up"> 
      <p id="songLyrics"></p>
    </div>
	</div>
  <br><br>
</div>
</#assign>
<#include "main.ftl">
