//<![CDATA[
	     
	 function disableAllTheseDays(date) {
		 alert("we are in the function");
		 
		 var disabledDays = ["7-15-2015", "7-23-2015"];   
		 
		 var m = date.getMonth(), d = date.getDate(), y = date.getFullYear();
	         for (i = 0; i < disabledDays.length; i++) {
	             if($.inArray((m+1) + '-' + d + '-' + y,disabledDays) != -1) {
	                 return [false];
	             }
	         }
	         return [true];
	     }
	//]]>