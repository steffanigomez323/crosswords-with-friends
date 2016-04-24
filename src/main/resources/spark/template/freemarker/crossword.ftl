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
			<#assign start=false>
			<textarea class = "box c${col_index} r${row_index}" spellcheck="false" maxlength="1" ></textarea>
		    <#list col.clues as clue>
				<#if clue.clue??>
					<#assign start=true>
				</#if>
			</#list>
				
			<#if start>
		    	<div class="numMarker">${num}</div>
		    	<#assign num = num + 1>
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
		<#assign start=false>
		<#if col.clues?? &&  (col.clues?size > 0)>
			<#list col.clues as clue>
					<#if clue.clue?? && clue.orientation == "ACROSS" >
						<li class="across">${clue.orientation} ${num} : ${clue.clue}</li>
						<#assign start=true>
					<#elseif clue.clue?? >
						<li class="down">${clue.orientation} ${num} : ${clue.clue}</li>
						<#assign start=true>
					</#if>
			</#list>
			<#if start>
		    	<#assign num = num + 1>
		    </#if>
		</#if>
	</#list>
</#list>
</ul>
</div>
<iframe id="ifrm" src="/chat" align="center"></iframe>
<script>console.log("room number");</script>