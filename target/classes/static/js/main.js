function checkCol(c, row, x, check){
	
	var foundUp = false;
	var foundDown = false;
	
	var down = row+1;
	var up=row-1;
	
	var block = c[row];
	
	var y = -1;
	var size = 1;
	
	$(block).addClass("wordActive");
	var word = $(block).val();
	var rSize = rowSize($(block).attr("class").split(" "));
	if (rSize>0){
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
				if (y==-1){
					var rSize = rowSize($(block).attr("class").split(" "));
					if (rSize>0){
						y = up;
					}
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
	
	console.log(x+" "+y);
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
	
	$(block).addClass("wordActive");
	var word = $(block).val();
	var cSize = colSize($(block).attr("class").split(" "));
	if (cSize>0){
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
				if (x==-1){
					var cSize = colSize($(block).attr("class").split(" "));
					if (cSize>0){
						x = left;
					}
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
	
	console.log(x+" "+y);
	if (check && size>1 && word.length == size){
		checkWord(word, x, y, "ACROSS");
	}
	if (!check){
		return row;
	}
}

function checkWord(word, x, y, o){
	console.log(word+" "+x+" "+y+" "+o);
	var id = $(".crossword").attr("id");
	console.log("id " + id);
	$.get("/check", {word: word, x: x, y: y, orientation: o, id:id}, function(response) {
        var res = JSON.parse(response);
        if (res){
        	
        	var classes = $(".active").attr("class").split(" ");
        	var row = parseFloat(classes[2][1]);
        	var col = parseFloat(classes[1][1]);
        	
        	if (orientation == "down"){
        		
        		var c = checkCol($("."+classes[1]), row, col, false);
        		$(c).attr("disabled", "disabled");
        		$(c).addClass("inactive");
        	} else if (orientation == "across"){
        		var r = checkRow($("."+classes[2]), col, row, false);
        		$(r).attr("disabled", "disabled");
        		$(r).addClass("inactive");
        	}
        }
    });
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

function rowSize(classes){
	var found = null
	for (i in classes){
		var c = classes[i];
		if (c.startsWith("D")){
			found = c;
			break;
		}
	}
	if (found != null){
		return parseFloat(found.split("N")[1])-1;
	}
	return 0;
}

function colSize(classes){
	var found = null
	for (i in classes){
		var c = classes[i];
		if (c.startsWith("A")){
			found = c;
			break;
		}
	}
	if (found != null){
		return parseFloat(found.split("S")[2])-1;
	}
	return 0;
}

function next(dir){
	var curr = $(".active");
	$(".active").removeClass("active");
	var classes = curr.attr("class").split(" ");
	
	var row = parseFloat(classes[2][1]);
	var col = parseFloat(classes[1][1]);
	
	var startX = null;
	var startY = null;
	
	if (orientation == "down"){
		var c = $(".c"+col);

		var next;
		if (dir == -1){
			next = row-1;
		} else {
			next = row+1;
		}
		
		if (next<0){
			console.log("1");
			next = row+rowSize(classes);
		} else if (next == numCol){
			console.log("2");
			next = row-rowSize(classes);
		}

		var block = c[next];
		
		if ($(block).hasClass("filled")){
			console.log(classes);
			var size = rowSize(classes);
			if (dir == -1){
				console.log("3");
				next = row+size;
			} else {
				console.log("4");
				next = row-size;
			}
			block = c[next];
		}
		
		checkCol(c, row, col, true);
		
		$(block).addClass("active");
		$(block).focus();
		
	} else if (orientation == "across"){
		var r = $(".r"+row);

		var next;
		if (dir == -1){
			next = col-1;
		} else {
			next = col+1;
		}
		
		if (next<0){
			console.log("1b");
			next = col+colSize(classes);
		} else if (next == numRow){
			console.log("2b");
			next = col-colSize(classes);
		}

		var block = r[next];
		
		if ($(block).hasClass("filled")){
			var size = colSize(classes);
			if (dir == -1){
				console.log("3b");
				next = col+size;
			} else {
				console.log("4b");
				next = col-size;
			}
			block = r[next];
		}
		
		checkRow(r, col, row, true);
		
		$(block).addClass("active");
		$(block).focus();
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