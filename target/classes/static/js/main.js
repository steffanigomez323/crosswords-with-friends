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
	var nex = 
	return "r"+nextRow(row)
}

function nextCol(col){
	return (col+1)%numCol;
}

function next(){
	var classes = $(".active").attr("class").split(" ");
	$(".active").removeClass("active");
	var row = row = parseFloat(classes[2][1]);
	var col = parseFloat(classes[1][1]);
	if (orientation == "down"){
		var col = $(".c"+col);
		var next = "r"+nextRow(row);
		for (var i=0; i<numCol; i++){
			var block = col[i];
			if ($(block).hasClass(next)){
				$(block).addClass("active");
				$(block).focus();
			}
		}
	} else if (orientation == "across"){
		//$(".c"+nextCol(col)+" .r"+row).addClass("active");
		var row = $(".r"+row);
		var next = "c"+nextCol(col);
		for (var i=0; i<numRow; i++){
			var block = row[i];
			if ($(block).hasClass(next)){
				$(block).addClass("active");
				$(block).focus();
			}
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
	        	orientation = "across";
	        	orient();
	        	break;
	        case 38:
	        	orientation = "down";
	        	orient();
	        	break;
	        case 39:
	        	orientation = "across";
	        	orient();
	        	break;
	        case 40:
	        	orientation = "down";
	        	orient();
	        	break;
	        default:
	        	if (event.keyCode>64 && event.keyCode<90){
	        		$(this).val(String.fromCharCode(event.keyCode));
	        		next();
	        	}
	     }
	     event.preventDefault();
	 });
	
}