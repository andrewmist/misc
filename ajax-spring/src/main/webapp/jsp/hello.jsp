<jsp:useBean id="message" scope="request" type="java.lang.String"/>
<html>
<head>
  <title>Spring MVC Ajax Demo</title>  
  <script src="http://code.jquery.com/jquery-latest.min.js"></script>
  <script type="text/javascript">
    function doAjax() {
      $.ajax({
        url: 'time.html',
        data: ({name : $("#txt").val()}),
        success: function(data) {
          $('#time').html(data);
        }
      });
    }
  </script>
</head>
<body>
${message}
<br>
<button id="demo" onclick="doAjax()" title="Button">Get the time!</button>
<br>
Enter your name: <input id="txt" value="Andrew Mist" />
<br>
<div id="time"></div>
</body>
</html>