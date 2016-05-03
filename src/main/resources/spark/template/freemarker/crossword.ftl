<link href='https://fonts.googleapis.com/css?family=Montserrat' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="css/style.css">
<div id="timer">10:00</div>
<div id="player" class="${players}">${player}</div>
<div id=${id} class="crossword">
<div id="crosswordWrapper">
<#assign num=1>
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
		    	<div class="numMarker <#if across>ACROSS</#if> <#if down>DOWN</#if>">${num}</div>
		    	<#assign num = num + 1>
		    </#if>
		</#if>
		</div>
	</#list>
	</div>
</#list>
</div>
<ul id="clues">
<#assign num=1>
<#list crossword as row>
	<#list row as col>
		<#assign start=false>
		<#if col.clues?? &&  (col.clues?size > 0)>
			<#list col.clues as clue>
					<#if clue.clue?? && clue.orientation != player >
						<li>${clue.orientation} ${num} : ${clue.clue}</li>
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
<div id="chatWrapper">
    <div id="chatControls">
        <input id="message" placeholder="Type your message">
        <button id="send">Send</button>
    </div>
    <ul id="userlist"> <!-- Built by JS --> </ul>
    <div id="chat">    <!-- Built by JS --> </div>
</div>
<script src="js/jquery-2.1.1.js"></script>
<script src="/js/websocketDemo.js"></script>
<script src="js/main.js"></script>