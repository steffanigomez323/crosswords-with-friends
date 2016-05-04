//TO DO
//Dissallow one-player orientation switching when not possible
//Double clues error
//Censoring words by room id
//clear caches on exit


//Establish the WebSocket connection and set up event handlers
var players = $("#player").attr("class");
var url = "ws://" + location.hostname + ":" + location.port + "/chat/";
if (players == "double"){
	url+="two";
} else {
	url+="one";
}
var webSocket = new WebSocket(url);
webSocket.onmessage = function (msg) { console.log("in on message"); updateChat(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed ") };

function time(stop){
	  var t = Date.parse(stop) - Date.parse(new Date());
	  var seconds = Math.floor((t/1000)%60);
	  var minutes = Math.floor((t/1000/60)%60);
	  return {
	    'minutes': minutes,
	    'seconds': seconds
	  };
}

function n(n){
    return n > 9 ? "" + n: "0" + n;
}

function countdown(stop, timer){
	var timeLeft = time(stop);
	
	$("#timer").text(n(timeLeft["minutes"])+":"+n(timeLeft["seconds"]));
	if (timeLeft["minutes"]==0 && timeLeft["seconds"]==0){
		alert("you lose :(");
		clearInterval(timer);
	}
}

function wordSize(classes, o){
	var found = null
	for (i in classes){
		var c = classes[i];
		if (o=="down" && c.startsWith("D")){
			found = c;
			break;
		} else if (o=="across" && c.startsWith("A")){
			found = c;
			break;
		}
	}
	if (found != null){
		if (o=="down"){
			return parseFloat(found.split("N")[1])-1;
		} else {
			return parseFloat(found.split("S")[2])-1;
		}
	}
	return 0;
}

function checkCol(c, row, x, check){

	var foundUp = false;
	var foundDown = false;
	
	var down = row+1;
	var up=row-1;
	var block = c[row];
	
	var y = -1;
	var size = 1;
	
	var word = $(block).val();
	var cSize = wordSize($(block).attr("class").split(" "), "down");

	if (cSize>0){
		y = row;
	}
	
	var col = [];
	col.push(block);

	while (!foundUp || !foundDown){
		if (up<0){
			foundUp=true;
		} else {
			block = c[up];
			if (!$(block).hasClass("filled")){
				word = $(block).val()+word;
				var cSize = wordSize($(block).attr("class").split(" "), "down");
				if (cSize>0){
					y = up;
				}
				col.push(block);
				up--;
				size++;
			} else {
				foundUp = true;
			}
		}
		if (down==numRow){
			foundDown=true;
		} else {
			block = c[down];
			if (!$(block).hasClass("filled")){
				word = word + $(block).val();
				col.push(block);
				down++;
				size++;
			} else {
				foundDown = true;
			}
		}
	}

	if (check && size>1 && word.length == size){
		checkWord(word, x, y, "DOWN");
	}
	if (!check){
		return col;
	}
}

//bug when on last index;
//bug when filling out with already filled letters?

function checkRow(r, col, y, check){
	
	var foundLeft = false;
	var foundRight = false;
	
	var right = col+1;
	var left=col-1;
	
	var block = r[col];

	var x = -1;
	var size = 1;
	
	var word = $(block).val();
	var rSize = wordSize($(block).attr("class").split(" "), "across");
	if (rSize>0){
		x = col;
	}
	
	var row = [];
	row.push(block);
	
	while (!foundLeft || !foundRight){
		if (left<0){
			foundLeft=true;
		} else {
			block = r[left];
			if (!$(block).hasClass("filled")){
				word = $(block).val()+word;
				var rSize = wordSize($(block).attr("class").split(" "), "across");
				if (rSize>0){
					x = left;
				}
				row.push(block);
				left--;
				size++;
			} else {
				foundLeft = true;
			}
		}
		if (right==numCol){
			foundRight=true;
		} else {
			block = r[right];
			if (!$(block).hasClass("filled")){
				word = word + $(block).val();
				row.push(block);
				right++;
				size++;
			} else {
				foundRight = true;
			}
		}
	}
	
	if (check && size>1 && word.length == size){
		checkWord(word, x, y, "ACROSS");
	}
	if (!check){
		return row;
	}
}

function checkWord(word, x, y, o){
	var id = $(".crossword").attr("id");
	var toSend = "DATA;"+word+";"+x+";"+y+";"+o+";"+id;
	console.log(word +". X : "+x+" Y : "+y+" O : "+o);
	webSocket.send(toSend);
}

function exposeLetter(x, y, o){
	var id = $(".crossword").attr("id");
	var toSend = "LETTER;"+x+";"+y+";"+o+";"+id;
	webSocket.send(toSend);
}

function wordActive(block){
	$(block).addClass("wordActive");
}

function orient(){
	var classes = $(".active").attr("class").split(" ");
	var row = parseFloat(classes[2][1]);
	var col = parseFloat(classes[1][1]);
	$(".wordActive").removeClass("wordActive");
	if (orientation == "down"){
		wordActive(checkCol($("."+classes[1]), row, col, false));
	} else if (orientation == "across"){
		wordActive(checkRow($("."+classes[2]), col, row, false));
	}
}


function next(dir){
	var curr = $(".active");
	var classes = curr.attr("class").split(" ");
	
	var row = parseFloat(classes[2][1]);
	var col = parseFloat(classes[1][1]);
	var word = $(".c"+col);
	
	if (orientation == "across"){
		word = $(".r"+row);
		var temp = col;
		col = row;
		row = temp;
	}
	
	var block = getNext(dir, col, row, word, classes);
	
	if (orientation == "down"){
		checkCol(word, row, col, true);
	} else {
		checkRow(word, row, col, true);
	}
	
	$(".active").removeClass("active");
	$(block).addClass("active");
	$(block).focus();
}

function getNext(dir, i, j, word, classes){
	
	var next;
	if (dir == -1){
		next = j-1;
	} else {
		next = j+1;
	}
	
	if (next<0){
		next = j+wordSize(classes, orientation);
	} else if (next == numRow){
		next = j-wordSize(classes, orientation);
	}
	
	var block = word[next];
	
	if ($(block).hasClass("filled")){
		var size = wordSize(classes, orientation);
		if (dir == -1){
			next = j+size;
		} else {
			next = j-size;
		}
		block = word[next];
	}
	if ($(block).attr("disabled")=="disabled"){
		return getNext(dir, i, next, word, $(block).attr("class").split(" "));
	}
	return block;
}

function startTimer(){
	var stop = new Date();
	stop.setMinutes(stop.getMinutes() + 15);
	var timer = setInterval(function(){
		countdown(stop, timer);
	}, 1000);
}

function getAllPlayerWords(start, o){
	var classes = $(start).attr("class").split(" ");
	var row = parseFloat(classes[2][1]);
	var col = parseFloat(classes[1][1]);
	var size = 0;
	if (o == "ACROSS"){
		size = wordSize(classes, "across");
	} else {
		size = wordSize(classes, "down");
	}	
	
	var id = $(".crossword").attr("id");
	return toSend = "ANAGRAM;"+size+";"+col+";"+row+";"+o+";"+id;
	
}

var orientation = "down";
var numCol = 0;
var numRow = 0;
var numWords = parseFloat($("#clues").attr("class").split("l")[1]);
var foundWords = 0;
var loading = 0;

window.onload = function(response) {	

	$("hint1").click(function(){
		$.get( "/hint1", function(data ) {
			$("textarea").click(function(){
				$(".active").removeClass("active");
				$(this).addClass("active");
				orient();
			});
		});
		$("hint1").hide();
	});
	
	$("hint2").click(function(){
		$.get( "/hint2", function(data ) {
			
		});
		$("hint2").hide();
	});
	
	$("hint3").click(function(){
		$.get( "/hint3", function(data ) {
			
		});
		$("hint3").hide();
	});
	
	numRow = $(".row").length;
	if (numRow>0){
		numCol = $(".r0").length;
	}
	
		
	if (players == "double"){
		
		var player = $("#player").text();
		
		var playersWords = [];
		
		$("."+player).each(function(){
			playersWords.push(this);
		});
		
		for (word in playersWords){
			console.log(player+" "+$(playersWords[word]).text());
			var toSend = getAllPlayerWords($(playersWords[word]).prev(), player);
			console.log(toSend);
			//webSocket.send(toSend);
		}
		
		var playerWords = $("."+player).prev().each(function(){
			var classes = $(this).attr("class").split(" ");
			var row = parseFloat(classes[2][1]);
			var col = parseFloat(classes[1][1]);
			var word = $(".c"+col);
			var enabled;
			if (player == "ACROSS"){
				orientation = "across";
				word = $(".r"+row);
				enabled = checkRow(word,col, row, false);
			} else {
				enabled = checkCol(word, row, col, false);
			} 
			$(enabled).attr("disabled", false);
		});
		$("#wait").toggle();
		
	} else {
		
		var playersWords = [];
		
		$(".numMarker").each(function(){
			playersWords.push(this);
		});
		
		for (word in playersWords){
			var classes = $(playersWords[word]).attr("class").split(" ");
			for (c in classes){
				if (c>0 && classes[c]!=""){
					console.log(classes[c]+" "+$(playersWords[word]).text());
					var toSend = getAllPlayerWords($(playersWords[word]).prev(), classes[c]);
					console.log(toSend);
					//webSocket.send(toSend);
				}
			}
		}
		
		$("textarea").attr("disabled", false);
		startTimer();
	}
	
	$("textarea").click(function(){
		$(".active").removeClass("active");
		$(this).addClass("active");
		orient();
	});
	
	$("textarea").keydown(function(event) {
	     switch(event.keyCode){
	        case 37:
	        	if (orientation=="across"){
	        		next(-1);
	        	}else {
	        		if (players == "single"){
			        	orientation = "across";
			        	orient();
	        		}
	        	}
	        	break;
	        case 38:
	        	if (orientation=="down"){
	        		next(-1);
	        	}else {
	        		if (players == "single"){
			        	orientation = "down";
			        	orient();
	        		}
	        	}
	        	break;
	        case 39:
	        	if (orientation=="across"){
	        		next(1);
	        	}else {
	        		if (players == "single"){
			        	orientation = "across";
			        	orient();
	        		}
	        	}
	        	break;
	        case 40:
	        	if (orientation=="down"){
	        		next(1);
	        	}else {
	        		if (players == "single"){
			        	orientation = "down";
			        	orient();
	        		}
	        	}
	        	break;
	        case 8:
	        	$(this).val("");
	        default:
	        	if (event.keyCode>64 && event.keyCode<91){
	        		$(this).val(String.fromCharCode(event.keyCode));
	        		next();
	        	}
	     }
	     event.preventDefault();
	 });
	
}