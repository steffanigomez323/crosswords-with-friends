window.onload = function(response) {	
	$("#showInstructions").click(function(){
		console.log("here!");
		$("#instructions").toggle();
	});
	
	$($("#instructions")).click(function(){
		$("#instructions").toggle();
	});
}