<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet' type='text/css'>
<link href='https://fonts.googleapis.com/css?family=Slabo+27px' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="css/style.css">
<div id="timer">10:00</div>
<div id="player" class="single">single player</div>
<div id="end">end game & show answers</div>
<div id=${id} class="crossword">
<div id="crosswordWrapper">
<#assign num=1>
<#assign total=0>
<#list crossword as row>
		<div class="row">
	<#list row as col>
		<div class="boxWrap">
		<#if col.isBox >
			<div class = "box filled c${col_index} r${row_index}"></div>
		<#else>
			<#assign start=false>
			<#assign across=false>
			<#assign down=false>
			<textarea disabled = "disabled" class = "box c${col_index} r${row_index} <#list col.clues as clue>${clue.orientation}${clue.size} <#if clue.clue??><#assign start=true><#if clue.orientation=="ACROSS"><#assign across=true><#else><#assign down=true></#if></#if></#list>" spellcheck="false" maxlength="1" ></textarea>
				
			<#if start>
		    	<div class="numMarker <#if across>ACROSS<#assign total = total + 1></#if> <#if down>DOWN<#assign total = total + 1></#if>">${num}</div>
		    	<#assign num = num + 1>
		    </#if>
		</#if>
		</div>
	</#list>
	</div>
</#list>
<br>
<div id = "hint1">Expose letter</div>
<!--<div id = "hint2">Get letters of word</div>
<ul id = "anagramList">
<div class = "anagramChoice"></div>
</ul>
<div id = "hint3">Expose clue</div>-->
</div>

<div id = "leftWrapper">
<ul id="clues" class="total${total} clueACROSS">
<span style="color:white">ACROSS</span>
<#assign num=1>
<#list crossword as row>
	<#list row as col>
		<#assign start=false>
		<#if col.clues?? &&  (col.clues?size > 0)>
			<#list col.clues as clue>
					<#if clue.clue?? && clue.orientation=="ACROSS">
						<li>${num} : ${clue.clue}</li>
						<#assign start=true>
					<#elseif clue.clue??>
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
<ul id="clues" class="total${total} clueDOWN">
<span style="color:white">DOWN</span>
<#assign num=1>
<#list crossword as row>
	<#list row as col>
		<#assign start=false>
		<#if col.clues?? &&  (col.clues?size > 0)>
			<#list col.clues as clue>
					<#if clue.clue?? && clue.orientation=="DOWN">
						<li>${num} : ${clue.clue}</li>
						<#assign start=true>
					<#elseif clue.clue??>
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
</div>

<div id="win"><div id="winText">YOU WON!</div><a id="newGame" href="../home">new game</a></div>
<script src="js/jquery-2.1.1.js"></script>
<script src="/js/websocketDemo.js"></script>
<script src="js/main.js"></script>