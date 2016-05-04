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
        if (foundWords == numWords){
        	alert("YOU WIN!");
        }
        
    } else if (msg.data.startsWith("LETTER;")) {
    	console.log("msg data " + msg.data);
    	var data = msg.data.split(";");
    	var col = parseFloat(data[1]);
    	var row = parseFloat(data[2]);
    	var letter = data[3];
    	$(".c"+col+".r"+row).val(letter);
    	$(".c"+col+".r"+row).attr("disabled", "disabled");
    	console.log("should have exposed letter ");
    	//$("#hint1").html("the letter at row " + row + ", column " + col + " is: " +letter);
    } else if (msg.data.startsWith("ANAGRAM")) {
    	console.log("msg data " + msg.data);
    	var data = msg.data.split(";");
    	var col = parseFloat(data[1]);
    	var row = parseFloat(data[2]);
    	var orientation = data[3];
    	var anagram = data[4];
    	var classes = $(".active").attr("class").split(" ");
    	$("#hint2").html("the letters of across 3 are" + anagram);
    	$("#hint2").css("background-color", "transparent");
    	$("#hint2").off();
    }
    else {
		if (players == "double"){
			if (loading < 2){
				loading ++;
				var player = $("#player").text();
				if (player == "ACROSS"){
					loading = 2;
					$("#wait").toggle();
					startTimer();
				} else if (loading==2){
					$("#wait").toggle();
					startTimer();
				} 
			}
			var data = JSON.parse(msg.data);
		      console.log(data.userMessage);
		      insert("chat", data.userMessage);
		      console.log("inserted");
		      data.userlist.forEach(function (user) {
		          insert("userlist", "<li>" + user + "</li>");
		      });
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
