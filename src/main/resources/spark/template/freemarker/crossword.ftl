<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="css/style.css">
<div id="timer">5:00</div>
<div id="end" class="hiddenEnd">end game & show answers</div>
<div id="alert"><#if player =="ACROSS">DOWN<#else>ACROSS</#if> <span>exited the game</span>. You have been converted to a one player.<img id="remove" src="css/x.png"></img></div>
<div id="end2">new game</div>
<div id="player" class="double">${player}</div>
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
<div class = "hints">
<div id = "stuck">Stuck?</div><p>
<div id = "hint1">Expose letter</div>
<div id = "hint2">Get letters of word</div>
<ul id = "anagramList">
<div class = "anagramChoice"></div>
</ul>
<div id = "hint3">Expose clue</div>
</div>
</div>
<div id = "leftWrapper">
<ul id="clues" class="total${total}">
<span style="color:white"><#if player =="ACROSS">DOWN<#else>ACROSS</#if> CLUES</span>
<#assign num=1>
<#list crossword as row>
	<#list row as col>
		<#assign start=false>
		<#if col.clues?? &&  (col.clues?size > 0)>
			<#list col.clues as clue>
					<#if clue.clue?? && clue.orientation != player >
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
<div id="chatWrapper">
    <div id="chatControls">
        <input id="message" placeholder="Type your message">
    </div>
    <div id="chat"></div>
    </div>
</div>
</div>




</div>
<div id="wait"><div id="waitText"><img id="loading" src="css/squares.svg"></img><br>waiting for second player</div></div>
<div id="win"><div id="winText">YOU WON!<br><a id="newGame" href="../home">new game</a></div></div>
<div id="lose"><div id="loseText">Sorry, you lose. :(<br><div id="answers">show answers</div><br><div id="continue">continue playing</div></div></div>
<script src="js/jquery-2.1.1.js"></script>
<script src="/js/websocketDemo.js"></script>
<script src="js/main.js"></script>