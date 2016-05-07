var players = $("#player").attr("class");
//Send message if "Send" is clicked
if (players == "double"){
	//Send message if enter is pressed in the input field
	id("message").addEventListener("keypress", function (e) {
	    if (e.keyCode === 13) { sendMessage(e.target.value); }
	});
}


//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "" && players == "double") {
    	console.log("typed + message" + message);
        webSocket.send(message);
        id("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
	console.log("recieved");
    if (msg.data.startsWith("DATA;")){
    	var data = msg.data.split(";");
    	var word = data[1];
    	var col = parseFloat(data[2]);
    	var row = parseFloat(data[3]);
    	var orientation = data[4];

        if (orientation == "DOWN"){
        	var c = checkCol($(".c"+col), row, col, false);
        	for (i in c){
        		$(c[i]).val(word[i]);
        	}
        	$(c).attr("disabled", "disabled");
            $(c).addClass("inactive");
        } else if (orientation == "ACROSS"){
        	var r = checkRow($(".r"+row), col, row, false);
        	for (i in r){
        		$(r[i]).val(word[i]);
        	}
        	$(r).attr("disabled", "disabled");
        	$(r).addClass("inactive");
        }
        
        foundWords += 1;
        console.log(timer);
        if (foundWords == numWords && timer){
        	$("#win").toggle();
        	
        }
        
    } else if (msg.data.startsWith("LETTER;")) {
    	console.log("msg data " + msg.data);
    	var data = msg.data.split(";");
    	var col = parseFloat(data[1]);
    	var row = parseFloat(data[2]);
    	var letter = data[3];
    	$(".c"+col+".r"+row).val(letter);
    	$(".c"+col+".r"+row).attr("disabled", "disabled");
    	$(".c"+col+".r"+row).addClass("inactive");
    	console.log("should have exposed letter ");
    } else if (msg.data.startsWith("ANAGRAM")) {
    	console.log("msg data " + msg.data);
    	var data = msg.data.split(";");
    	var col = parseFloat(data[1]);
    	var row = parseFloat(data[2]);
    	var orientation = data[3];
    	var wordId = data[4];
    	var anagram = data[5];
    	$("#anagramList").hide();
    	$("#hint2").html("the letters of " + orientation + " " + wordId + " are " + "'" + anagram + "'");
    	$("#hint2").off();
    	$("#hint2").css("background-color", "transparent");
    } else if (msg.data.startsWith("**ALL**")) {
    	console.log("showing all");
    	console.log(msg.data);
    	var data = msg.data.split(":")[1].split("\n");
    	for (row in data){
    		var cols = data[row].split(" ");
    		for (col in cols){
    			var letter = "S";
    			$(".c"+col+".r"+row).val(cols[col]);
    			$(".c"+col+".r"+row).attr("disabled", "disabled");
    		}
    	}
    	clearInterval(timerGlobal);
    	$("#end").text("new game");
    	$("#end2").toggle();
    } else if (msg.data.startsWith("**END**")) {
    	var data = msg.data.split(":");
    	var other = data[1];
    	console.log(other);
    	if (other=="show"){
			$("#alert span").text("chose not to continue");
			convertToOnePlayer();
    	} else {
    		$("#end").toggle();
    	}
    } else if (msg.data.startsWith("**CONVERT**")) {
    	if (!$("#end").is(":visible")){
    		$(".hiddenEnd").toggle();
    	}
    	players = "single";
    	$("textarea").attr("disabled",false);
    	$(".inactive").attr("disabled","disabled");
    	var clues = msg.data.split("/:/");
    	var player = $("#player").text();
    	console.log(clues.length-2);
    	var total = clues.length-2;
		var html = "<ul id='clues' class='total"+total+"'>";
		html+= "<span style='color:white'>"+player+" CLUES</span>";
    	for (var i in clues){
    		if (i>0 && i<clues.length-1){
				var data = clues[i].split(";");
				var o = data[2];
				if (o==player){
					var col = data[0];
					var row = data[1];
					var clue = data[3];
					html+="<li>"+$(".c"+col+".r"+row).next().text()+" : "+clue+"</li>";
				}
    		}
    	}
    	html+="</ul>";
		$(html).insertAfter("#clues");
		timer = false;
		timerGlobal = false;
    	
    } else {
		if (players == "double"){
			if (loading < 2){
				loading ++;
				var player = $("#player").text();
				if (player == "ACROSS"){
					loading = 2;
					$("#wait").toggle();
					timerGlobal = startTimer();
				} else if (loading==2){
					$("#wait").toggle();
					timerGlobal = startTimer();
				} 
			}
			var data = JSON.parse(msg.data);
		      var innerMessage = data.userMessage.split("<p>")[1].split("</p>")[0];
		      console.log("."+innerMessage+".");
		      if (innerMessage=="down left the chat " || innerMessage=="across left the chat "){
		    	  if (!$("#end2").is(":visible")){
			    	  convertToOnePlayer();
		    	  }
		      }
		      insert("chat", data.userMessage);
		}
    }
}


//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}
