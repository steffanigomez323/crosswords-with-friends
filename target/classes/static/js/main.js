function checkWord(word, x, y, o){
	console.log(word+" "+x+" "+y+" "+o);
	$.get("/check", {word: word, x: x, y: y, orientation: o, id:"abcdef"}, function(response) {
        var res = JSON.parse(response);
        console.log(res);
    });
}

function orient(){
	var classes = $(".active").attr("class").split(" ");
	$(".wordActive").removeClass("wordActive");
	if (orientation == "down"){
		$("."+classes[1]).addClass("wordActive");
	} else if (orientation == "across"){
		$("."+classes[2]).addClass("wordActive");
	}
}

function nextRow(row){
	return (row+1)%numRow;
}

function prevRow(row){
	row = row-1;
	if (row < 0){
		row = numRow -1;
	}
	return row;
}


function nextCol(col){
	return (col+1)%numCol;
}

function prevCol(col){
	col = col-1;
	if (col < 0){
		col = numCol -1;
	}
	return col;
}

function next(dir){
	var classes = $(".active").attr("class").split(" ");
	$(".active").removeClass("active");
	
	var row = row = parseFloat(classes[2][1]);
	var col = parseFloat(classes[1][1]);
	
	var found = false;
	var done = true;
	var word = "";
	
	var startX = null;
	var startY = null;
	
	if (orientation == "down"){
<<<<<<< HEAD
		var c = $(".c"+col);
=======
		var col = $(".c"+col);
>>>>>>> ad508f8ea9eb4f8ca269058cc48fb2d242f59746
		var next;
		if (dir == -1){
			next = prevRow(row);
		} else {
			next = nextRow(row);
		}
<<<<<<< HEAD
		for (var i=0; i<numRow; i++){
			var block = c[i];
			if (!found){
				if ($(block).hasClass("r"+next)){
					if ($(block).hasClass("filled")){
						if (dir == -1){
							next = prevRow(next);
						} else {
							next = nextRow(next);
						}
						i=-1;
					} else {
						$(block).addClass("active");
						$(block).focus();
						found = true;
					}
				}
			}
			if (!$(block).hasClass("filled") && $(block).val() == ""){
				done = false;
			}
			else {
				if (!$(block).hasClass("filled") && startY == null){
					startX = col;
					startY = i;
				}
				word+= $(block).val();
=======
		for (var i=0; i<numCol; i++){
			var block = col[i];
			if ($(block).hasClass("r"+next)){
				if ($(block).hasClass("filled")){
					next = nextRow(next);
					i=-1;
				} else {
					$(block).addClass("active");
					$(block).focus();
					break;
				}
>>>>>>> ad508f8ea9eb4f8ca269058cc48fb2d242f59746
			}
		}
		if (done){
			checkWord(word, startX, startY, "DOWN");
		}
	} else if (orientation == "across"){
<<<<<<< HEAD
		var r = $(".r"+row);
=======
		var row = $(".r"+row);
>>>>>>> ad508f8ea9eb4f8ca269058cc48fb2d242f59746
		if (dir == -1){
			next = prevCol(col);
		} else {
			next = nextCol(col);
		}
<<<<<<< HEAD
		for (var i=0; i<numCol; i++){
			var block = r[i];
			if (!found){
				if ($(block).hasClass("c"+next)){
					if ($(block).hasClass("filled")){
						if (dir == -1){
							next = prevCol(next);
						} else {
							next = nextCol(next);
						}
						i=-1;
					} else {
						$(block).addClass("active");
						$(block).focus();
						found = true;
					}
				} 
			}
			if ($(block).val() == ""){
				done = false;
			} else {
				if (!$(block).hasClass("filled") && startX == null){
					startX = i;
					startY = row;
				}
				word+= $(block).val();
			}
=======
		for (var i=0; i<numRow; i++){
			var block = row[i];
			if ($(block).hasClass("c"+next)){
				if ($(block).hasClass("filled")){
					next = nextCol(next);
					i=-1;
				} else {
					$(block).addClass("active");
					$(block).focus();
					break;
				}
			} 
>>>>>>> ad508f8ea9eb4f8ca269058cc48fb2d242f59746
		}
		if (done){
			checkWord(word, startX, startY, "ACROSS");
		}
	}
}

var orientation = "down";
var numCol = 0;
var numRow = 0;

window.onload = function(response) {	
	
	numRow = $(".row").length;
	if (numRow>0){
		numCol = $(".r0").length;
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
		        	orientation = "across";
		        	orient();
	        	}
	        	break;
	        case 38:
	        	if (orientation=="down"){
	        		next(-1);
	        	}else {
		        	orientation = "down";
		        	orient();
	        	}
	        	break;
	        case 39:
	        	if (orientation=="across"){
	        		next(1);
	        	}else {
		        	orientation = "across";
		        	orient();
	        	}
	        	break;
	        case 40:
	        	if (orientation=="down"){
	        		next(1);
	        	}else {
		        	orientation = "down";
		        	orient();
	        	}
	        	break;
	        case 8:
	        	$(this).val("");
	        default:
	        	if (event.keyCode>64 && event.keyCode<90){
	        		$(this).val(String.fromCharCode(event.keyCode));
	        		next();
	        	}
	     }
	     event.preventDefault();
	 });
	
}