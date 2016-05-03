//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
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
    }
    else {
      var data = JSON.parse(msg.data);
      insert("chat", data.userMessage);
      console.log("inserted");
      id("userlist").innerHTML = "";
      data.userlist.forEach(function (user) {
          insert("userlist", "<li>" + user + "</li>");
      });
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
