<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<style>
	body{
		background-color:light-yellow;
	}
	
	span{
		background-color:gray;
		color:red;
		left-margin:200px;
		margin-right:200px;
	}
</style>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<!--script type="text/javascript" src="scripts/jquery-1.7.2.js" ></script-->
<script>
    $(document).ready(function() {
    	
    	$("#btn1").click(function(){
    		//springmvc
    		//hello_world
    		// /hello_world.html
    		$.get('/hello_world.html',{getLookup:'country'}, function(responseJson) {
    			alert('U clicked me 2!');
    		}); 	
    		
    		alert('U clicked me 3!');
    	});
    });
</script>
</head>
<body>
	<p>This is my message: ${message}</p>
	<p style="margine-right:50;margine-left:100">Readme: ${readme}</p>
	<br/>	
	<input type="button" id="btn1" value="Get web.xml"/>
	<br/>
	web.xml: <span>${webxml}</span>
	
</body>
</html>