<link rel="stylesheet" href="css/style.css">
<script src="js/jquery-2.1.1.js"></script>
<script src="js/main.js"></script>
<div id=${id} class="crossword">
<#list crossword as row>
		<div class="row">
	<#list row as col>
		<#if col.isBox >
			<div class = "box filled c${col_index} r${row_index}"></div>
		<#else>
			<textarea class = "box c${col_index} r${row_index}" spellcheck="false" maxlength="1" ></textarea>
		</#if>
	</#list>
	</div>
</#list>
</div>
<iframe id="ifrm" src="/chat" align="center"></iframe>
<script>console.log("room number");</script>