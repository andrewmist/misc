<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!--script type="text/javascript" src="scripts/jquery-1.7.2.js" ></script-->
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript" src="scripts/validator/jquery.validate.js"></script>
<title>LookupManager</title>
<style>
	table, th {
		border:1px solid black;		
		border-collapse: collapse;
	}
	th {
		background-color:gray;
		color:white;
		text-align:middle;		
	}
	#countryForm {
		width: 500px;
		b_order:1px solid black;	
	}
	#countryForm label.error {
		d_isplay: table-row;
		display: inline;
		color: red;
	}
</style>
<script>
	$.validator.setDefaults({
		debug:true,
		success:"valid"
	});

	$(document).ready(function() {
		$("#countryForm").validate({
			rules: {
				newCountry: {
					required: true,
					minlength: 3,
					maxlength: 20
				}
			},
			messages: {
				newCountry: {
					required: "Please enter New Country Name",
					minlength: "At least 3 chars, please",
					maxlength: "20 chars max, please"
				}
			}
		});

    	//alert("HERE");
		$.ajax({
			url: 'getCountries.html',		    			
			dataType: 'json',        		
			success: function(data) {
				buildCountriesTable(data);
				countriesAll = data;
			}
		});
    	
    	getProvances();
    });

    function getProvances(){
    	$.ajax({
			url: 'getProvances.html',
			dataType: 'json',        		
			success: function(data) {
				buildProvancesTable(data);
    		}
    	});
    }



    
    function buildCountriesTable(data){
    	$('#tblCountries tr').not(function(){if ($(this).has('th').length){return true}}).remove();
        $.each(eval(data), function(i, item) {
			//alert("KYKY: " + i + ":" + data.country_id + ", "+ data.country_name);
			var cId = item.country_id;
			var cName = item.country_name;
        	var tr = $("<tr></tr>");
        	var td1 = $("<td>").text(cId);
        	var td2 = $("<td>").text(cName);
        	td1.appendTo(tr);
        	td2.appendTo(tr);
        	
        	var td3 = $("<td align='center'>").html(getDelChk(cId));
        	td3.appendTo(tr);
        	tr.appendTo($("#tblCountries"));
        });  
        
        var trLast = $("<tr><td>&nbsp;</td><td align='center'></td><td align='center'></td></tr>"); 
        trLast.appendTo($("#tblCountries"));
        $("#tblCountries tr:last td:eq(1)").append(getBtnAdd());
        $("#tblCountries tr:last td:eq(2)").append(getBtnDelete());
    }
    
    function getDelChk(id){
    	return $("<input type='checkbox' id='chk_"+id+"'>");
    }
    
    function getBtnDelete(){
		var rslt = $('<button>Delete</button>').click(function () {
			var ask2Delete = "";
            $("#tblCountries input[type=checkbox]:checked").each(function () {
				var pureId = $(this).attr('id').replace('chk_', '');
                if (ask2Delete == "") {
                    ask2Delete = pureId;
                }else{
					ask2Delete += "," + pureId;
				}
            });
            
            //alert("PureIds: " + ask2Delete);
        	$.ajax({
    			url: 'deleteCountries.html',
    			data: ({'askingToDelete' : ask2Delete}),
    			dataType: "json",        		
    			success: function(data) {
    				//am refresh table somehow
    				//am I do not want to mess with adding/sorting/etc..    				
    				//alert("DELETED: " + data);
    				var msg = "";
    				$.each(eval(data), function(i, item) {
    					msg += (msg == "") ? item.country_name : ", " + item.country_name;
    				});
    				
    				alert(msg == "" ? "No Country is Deleted! \n(all got Provinces attached)" : "Deleted: " + msg);
					
		        	$.ajax({
		    			url: 'getCountries.html',		    			
		    			dataType: 'json',        		
		    			success: function(data) {
							countriesAll = data;
		    				buildCountriesTable(data);
							getProvances();
		        		}
		        	});
        		}
        	});
		});
		return rslt;
	}

	var countriesAll;
    function getBtnAdd(){
    	var btnCommitNew = $('<button>Commit</button>').click(function () {
			//alert("Commitin: " + $('#newCountry').val());
			
			var cForm = $('#countryForm');
			if (cForm.valid()){
				$.ajax({
					url: 'insertCountry.html',
					data: ({'countryName' : $('#newCountry').val()}),
					dataType: 'json',
					error: function (request, status, errorThrown) {
                        alert('Server Error: ' + errorThrown);
                    }, 
					success: function(data) {
						//alert("INSERTED: " + data.country_id + " " + data.country_name);
						$.ajax({
							url: 'getCountries.html',		    			
							dataType: 'json',        		
							success: function(data) {
								//am resetting countriesAll, rereading Provances tbl
								countriesAll = data;
								buildCountriesTable(data);
								getProvances();
							}
						});
					}

				});
			}
    	});
    	
    	var newRow = $("<tr style='text-align:center;background-color:lightgray' id='addCountryRow'><td colspan='3'><input id='newCountry' name='newCountry' type='text' value=''></td></tr>");
		newRow.find('td').append(btnCommitNew);
		
		var rslt = $('<button>Add</button>').click(function () {						
			if ($(this).html() != 'Cancel'){
				$(this).closest('tr').prev('tr').after(newRow);
				$(this).html('Cancel');
				$("#newCountry").focus();
			}else{
				$(this).html('Add');
				$('#addCountryRow').remove();
			}
			
		});
		return rslt;
	}


	
	//-- PROVANCES --
    function buildProvancesTable(data){
    	$('#tblProvances tr').not(function(){if ($(this).has('th').length){return true}}).remove();
    	
        $.each(eval(data), function(i, item) {
			//alert("KYKY: " + i + ":" + item[0] + ", " + item[1] + ": " + item.provance_id + " " + i);
        	var tr = $("<tr></tr>");
        	var td1 = $("<td>").text(item.country_id + "_" + item.provance_id);
        	var td2 = $("<td>").text(item.provance);
        	td1.appendTo(tr);
        	td2.appendTo(tr);
        	
        	//am moving from 'chk_2_3_Provance' to 'chk_i' (affect 'delete' f-n)
        	//var td3 = $("<td align='center'>").html(getDelChk(item.country_id+"_"+item.provance_id+"_"+item.provance));
        	var td3 = $("<td align='center'>").html(getDelChk(i));
        	td3.appendTo(tr);

        	var td4 = $("<td align='center'>").html(getCbxCountries(item.country_id));
        	td4.appendTo(tr);
        	tr.appendTo($("#tblProvances"));
        });  
		
        //am management Row
        var trLast = $("<tr><td>&nbsp;</td><td align='center'></td><td align='center'></td><td>&nbsp;</td></tr>"); 
        trLast.appendTo($("#tblProvances"));		
        $("#tblProvances tr:last td:eq(1)").append(getBtnAddProvance());
        $("#tblProvances tr:last td:eq(2)").append(getBtnDeleteProvances());
    }

	function getCbxCountries(cId){
		var rslt = $('<select />');
		$('<option>').val(-1).text('..Not Assigned...').appendTo(rslt);
		///am quit using $countries //$.each(eval('${countries}'), function(key, value) {
		$.each(eval(countriesAll), function(key, value) {		
			if (value.country_id == cId){				
				$('<option>').val(value.country_id).text(value.country_name).attr('selected','selected').appendTo(rslt);			
			}else{
				$('<option>').val(value.country_id).text(value.country_name).appendTo(rslt);			
			}
		});        	      

		
		rslt.change(function(event) {
			var newCountry = $(this).val();
			var oldCountry = cId;
			var cbx = $(this).parent();
			var pId = cbx.parent().find('td').html().split('_');
			
			if (newCountry != oldCountry){
				//alert('COUNTRY-PROVANCE-CHANGE oldCountry: ' + oldCountry + ', Provance: ' + pId[1] + ', newCounty: ' + newCountry);
				$.ajax({
					url: 'setProvinceToCountry.html',
					data: ({'curCountryId' : oldCountry, 'newCountryId' : newCountry, 'provinceId' : pId[1]}),
					dataType: 'json',        		
					success: function(data) {
						//am find and change values for cbx if used there!
						var newId = newCountry + "_" + data;						
						cbx.parent().find('td:eq(0)').html(newId);
					}
				});
			}
		});
		
		return rslt;
	}
	
    function getBtnDeleteProvances(){
		var rslt = $('<button>Delete</button>').click(function () {
			var ask2Delete = "";
            $("#tblProvances input[type=checkbox]:checked").each(function () {
				var row = $(this).attr('id').replace('chk_', '');
				var cpName = $(this).parent().parent().find('td:eq(0)').html();
				cpName += "_" + $(this).parent().parent().find('td:eq(1)').html();
				ask2Delete += (ask2Delete == "") ? cpName : "," + cpName;
            });
            
            //alert("PureIds: " + ask2Delete);            
        	$.ajax({
    			url: 'deleteProvinces.html',
    			data: ({'askingToDelete' : ask2Delete}),
    			dataType: 'json',        		
    			success: function(data) {
    				var msg = "";
    				$.each(eval(data), function(i, item) {
    					msg += (msg == "") ? item.provance : ", " + item.provance; 
    				});
    				
    				alert(msg == "" ? "No Provance is Deleted (all got Towns atatched)!" : "Deleted: " + msg);
    				
    				getProvances();
        		}
        	});

		});
		
		return rslt;
	}

    function getBtnAddProvance(){
    	var btnCommitNew = $('<button>Commit</button>').click(function () {
			var countryId = $(this).parent().find('select').val();
			//alert("Commitin: " + $('#newProvance').val() + ", Country: " + countryId);
			
        	$.ajax({
    			url: 'insertProvance.html',
    			data: ({'provanceName' : $('#newProvance').val(), 'countryId' : countryId}),
    			dataType: 'json',        		
    			success: function(data) {
    				//alert("INSERTED: " + data.country_id + " " + data.country_name);
		        	$.ajax({
		    			url: 'getProvances.html',		    			
		    			dataType: 'json',        		
		    			success: function(data) {
		    				buildProvancesTable(data);
		        		}
		        	});
        		}
        	});
			
    	});
    	
    	var newRow = $("<tr style='text-align:center;background-color:lightgray' id='addProvanceRow'><td colspan='4'>Add: <input id='newProvance' value=''></td></tr>");
		newRow.find('td').append('To:');
		newRow.find('td').append(getCbxCountries('-1'));
		newRow.find('td').append(btnCommitNew);
		
		var rslt = $('<button>Add</button>').click(function () {						
			if ($(this).html() != 'Cancel'){
				$(this).closest('tr').prev('tr').after(newRow);
				$(this).html('Cancel');
				$("#newProvance").focus();
			}else{
				$(this).html('Add');
				$('#addProvanceRow').remove();
			}
			
		});
		return rslt;
	}
</script>
</head>

<body>
<h3>Lookups Manager</h3>
<a style="margin-left:30px" href="index.html">Back</a>
<br>
<form id="countryForm">
	<table id="tblCountries" width="100%">
		<tbody>	
			<tr><th>Country Id</th><th>Country Name</th><th>Delete</th></tr>
		</tbody>
	</table>
</form>
<br>
<table id="tblProvances">
	<tbody>	
		<tr><th>Provance Id</th><th>Provance/State Name</th><th>Delete</th><th>Assign</th></tr>
	</tbody>
</table>


</body>
</html>