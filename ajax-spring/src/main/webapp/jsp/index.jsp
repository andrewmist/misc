<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
	button {
		margin: 3px;
		font-size : -1;
	}
	table,th,td	{
		border:1px solid black;
	}
	th {
		background-color:gray;
		color:white;
	}
	#towns
	{
	    border-collapse: collapse;
	}
	#towns td 
	{
	    padding:0;
	}	
	table tr
	{
		background-color:lightyellow;		
	}
	#towns .odd {
	   background: #ccc;
	}	
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AJAX Spring</title>
<!--script src="http://code.jquery.com/jquery-latest.min.js"></script-->
<script type="text/javascript" src="scripts/jquery-1.7.2.js" ></script>
<script>
    $(document).ready(function() {
    	//alert("THIS WHERE IS THIS MESASGE!!!!!!");

       	var $select = $('#country');
        $.each(jQuery.parseJSON('${countries}'), function(key, value) {
        	$('<option>').val(key).text(value).appendTo($select);
        });        	      

    	//Disable Town Form    	
    	$("#divTown").hide("slow");
    	//Disable Towns div    		
    	$("#tbl1").hide("slow");
    	
    	  
    	$("#addTown").click(function(event) {
			if ( ! $("#divTown").is(':hidden') ) {
				$("#divTown").hide("slow");
				$("#lblChk").text("Enable 'Add Town'");
				//$("#addTown").attr("checked", false);
				//this.checked = false;
			} else {
				$("#lblChk").text("Add Town"); 
				$("#divTown").show();
				//this.checked = true;
			}
		});    	
    	  
        
        
        //am Country-change
    	$('#country').change(function(event) { 
    		$("#tbl1").hide("slow");    	
    		
			//am resetting Form and damn chk    		
        	if (! $("#divTown").is(':hidden') ) {
        		resetTownForm()
            	$("#divTown").hide("slow");
            	$("#addTown").click();
            }
    		
        	var $countryId=$("select#country").val();
        	$("#spanProvanceOrState1").html($countryId == "1" ? "State" : "Province");
        	$("#spanProvanceOrState2").html($("#spanProvanceOrState1").html());
        	
        	$.ajax({
        		url: 'provances.html',
        		data: ({countryId : $countryId}),
        		success: function(data) {
        			var $select = $('#states');
               		$select.find('option').remove();
               		$('<option>').val(0).text("Select...").appendTo($select);
			        $.each(jQuery.parseJSON(data), function(key, value) {
			        	$('<option>').val(key).text(value).appendTo($select);
			        });        	      
          		}
        	});
        });


        //Provances-change: fill Towns table	
    	$('#states').change(function(event) {    		
    		try{
    			manageTownsTable();
    		}catch (e){
    			alert("ERR: " + e);		
    		}
        });





        function manageTownsTable(){
    		var $countryId=$("select#country").val();
            var $provanceId=$("select#states").val();

            //am fill Towns table
            $.ajax({
        		url: 'towns.html',
        		data: ({countryId : $countryId, provanceId : $provanceId}),
        		dataType: "json",        		
        		success: function(data) {
					//alert("TOWNS: " + data);
					
	            	$('#towns tr').not(function(){if ($(this).has('th').length){return true}}).remove();
	            	var $select = $('#towns');
                
	            	$.each(data, function(key, value) {
	                	var rowNew = $("<tr id='tr_" + value['town_id'] + "'><td></td><td></td><td></td><td></td></tr>");
	                	rowNew.children().eq(0).text(value['town_name']);                   		
	                	rowNew.children().eq(1).text(value['population']);                   		
	                	//alert(rowNew.html());
	
	                	var townId = value['town_id'];
	                	var z = "tr_" + townId;
	                	
	                	//am load detail form
	                	var btnEdit = getBtnEdit(townId, z);
	                	//var btnEdit = $('<button>Edmit '+ townId +'</button>');

						//am have Delete btn	                	
	                	var btnKill = getBtnKill($countryId, $provanceId, townId, z);
	                    
	                    //$("rowNew td:eq(2)").html(btnEdit);
	                   	rowNew.appendTo($select);                 
	                   	$("#towns tr:last td:eq(2)").append(btnEdit);
	                   	$("#towns td:last").append(btnKill);
	                   	//alert($select.html());
	                   	$("tr:odd").addClass("odd");
	                });
	            	
	                if ( $("#tbl1").is(':hidden') ) {    
	                	$("#tbl1").show();	                	
	                }
          		}
        	});
		}
        
        function getBtnEdit(townId, z){
        	var rslt = $('<button>Edit '+ townId +'</button>').click(function () {
            	try{
                	$("#lblChk").text("Edit Town");
                	$("#divTown").show();
                			
                	var tN = $("#towns").find("tr#"+z).find("td:eq(0)").html();
                	var ppl = $("#towns").find("tr#"+z).find("td:eq(1)").html();
                	$("#tName").val(tN);
                	$("#tPopulation").val(ppl);
                	$("#tId").val(townId);
                			
                	//alert("HTML: " + $("#town").html());
                	//alert("PRMS: cntry: " + $countryId + ", pro: " + $provanceId + ", townId: " + townId);
                }catch (e){
                	alert("ERROR: " + e);
                }
            });
        	return rslt;
        }
        
        function getBtnKill(countryId, provanceId, townId, z){
        	var rslt = $('<button>Delete</button>').click(function () {
            	//Permanent Delete
            	//alert(countryId + ", " + provanceId + ", " + townId + ", " +z)
            	var row = $(this).closest('tr');
            	$.ajax({
        			url: 'deleteTown.html',
        			data: ({'countryId' : countryId, 'provanceId' : provanceId, 'townId': townId}),
        			dataType: "json",        		
        			success: function(data) {
        				row.remove();
                       	if (! $("#divTown").is(':hidden') ) {
                       		resetTownForm();
                       		$("#addTown").click();
                       	}
            		}
            	});	
            	
        	});
        	
        	return rslt;
        }    
            
            
        function resetTownForm(){
        	//$("#addTown").attr("checked", false);
            $("#tName").val("");
            $("#tPopulation").val("");
            $("#tId").val("");
        }
    	
    	//add SaveTown OnClick
    	$("#saveTown").click(function(event) {
        	var countryId=$("select#country").val();
        	var provanceId=$("select#states").val();

        	var tName = $("#tName").val();
        	var tPpl = $("#tPopulation").val();
        	var tId = $("#tId").val();
        	
        	//alert("PRMS..HERE: cntry: " + $countryId + ", pro: " + $provanceId + ", town: " + tId);
			//am same method used to INSERT and UPDATE
           	$.ajax({
       			url: 'setTown.html',
				type: 'POST',
       			data: ({'countryId' : countryId, 'provanceId' : provanceId, 'townId': tId, 'tName':tName, 'tPopulation':tPpl}),
       			dataType: 'json',        		
       			success: function(data) {
       				manageTownsTable();
           		},
				error: function (jqXHR, textStatus, errorThrown){
					alert('CRAP: ' + textStatus);
				}
           	});	
        });
    });
            
    window.onerror = function(err){
    	alert("Window.Error: "  + err);        
    }
</script>
</head>
<body>
<h3>AJAX calls Spring Servlet which calls JDBCTemplate that returns GSON (jsp uses jQuery)</h3>	

	Countries:
	<select id="country">
		<option>Select...</option>		
	</select>
	<br/>
	<br/>
	<span id="spanProvanceOrState1">Provances/States</span>:
	<select id="states">
		<option>Not Available..</option>
	</select>
	<br>
	<a style="margin-left:50px" href="lookupManager.html">Lookup Manager</a>
	
	<br>
	<br>
	<div id="tbl1" style="display:block">
	Towns by Country/<span id="spanProvanceOrState2"></span>
	<table id="towns" cellspacing="0">		
		<tr><th>Name</th><th>Population</th><th>Edit</th><th>Delete</th></tr>			
	</table>
	<br/>
	<input type="checkbox" id="addTown"><label id="lblChk">Show/Hide 'Add Town'</label>
	<br/>
	</div>

	<div id="divTown" style="display:hidden">
		<input id="tId" type="hidden" value=""/>	
		<table id="town" cellspacing="0">			
			<tr>
				<th>Name:</th><td><input type="text" id="tName" value="Boom!"/></td>
			</tr>
			<tr>
				<th>Population:</th><td><input type="text" id="tPopulation" value="890"/></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input type="button" id="saveTown" value="Save Town"/>
				</td>
			</tr>
		</table>
	</div>
</body>

</html>
