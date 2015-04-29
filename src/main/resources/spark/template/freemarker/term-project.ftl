<#assign content>
<h1 class="title">Speech-to-Text Music Finder</h1>

<div class="wrapper">
    <div>
    <textarea id="searchInput" cols=50 rows=25>
    </textarea>
    <button id="recordButton" style="font: bold 14px Arial">Record</button>
    </div>
    <p id="resultUrl"></p>
    </br>
    <button id="searchButton" style="font: bold 14px Arial">Search</button>
</div>
</#assign>
<#include "main.ftl">