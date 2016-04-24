<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="css/style.css">
<script src="js/jquery-2.1.1.js"></script>
<script src="js/main.js"></script>
<div id=${id} class="crossword">
<#assign num=1>
<#list crossword as row>
		<div class="col">
	<#list row as col>
		<div class="boxWrap">
		<#if col.isBox >
			<div class = "box filled c${col_index} r${row_index}"></div>
		<#else>
		    <#if col.clues?? >
		    	<textarea class = "box c${col_index} r${row_index}" spellcheck="false" maxlength="1" ></textarea>
		    	<div class="numMarker">${num}</div>
				<#assign num = num + 1>
			<#else>
				<textarea class = "box c${col_index} r${row_index}" spellcheck="false" maxlength="1" ></textarea>
			</#if>
		</#if>
		</div>
	</#list>
	</div>
</#list>

<ul id="clues">
<#assign num=1>
<#list crossword as row>
	<#list row as col>
		<#if col.clues?? >
			<#list col.clues as clue>
					hi
					<#if clue.orientation == "ACROSS" >
						<li class="across">${clue.orientation} ${num} : ${clue.clue}</li>
					<#else>
						<li class="down">${clue.orientation} ${num} : ${clue.clue}</li>
					</#if>
			</#list>
			<#assign num = num + 1>
		</#if>
	</#list>
</#list>
</ul>
</div>
<iframe id="ifrm" src="/chat" align="center"></iframe>
<script>console.log("room number");</script>