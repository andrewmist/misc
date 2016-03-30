<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tilesx" uri="http://tiles.apache.org/tags-tiles-extras"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="<c:url value="/webresources/css/b2bClientPortal.css" />" rel="stylesheet">

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title><tiles:insertAttribute name="title" ignore="true" /></title>

<style type="text/css">
	<!--//am static goes here -->
</style>

	<!-- loading module-specific css from module-tiles, in tiles use name: stylesheets -->
	<tilesx:useAttribute id="cssFiles" name="stylesheets" classname="java.util.List" ignore="true"/>
	<c:if test="${not empty fn:trim(cssFiles)}">
	<c:forEach var="cssFile" items="${cssFiles}">
		<link rel="stylesheet" type="text/css" href="<c:url value="${cssFile}" />" />
	</c:forEach> 
	</c:if>
	
	<!-- //am -->
	<tilesx:useAttribute id="contentList" name="contents" classname="java.util.List" ignore="true"/>
</head>
<script type="text/javascript">
$(document).ready(function() {
});
	
</script>

<div id="main" class="wrapper">
	<a href="http://www.storedvalue.com/" class="notab">
		<img src="<c:url value="/webresources/images/svs_logo.png"/>" alt="Comdata - Stored Value Systems" />
	</a>
	<div>
		<tiles:insertAttribute name="hdr"/>
	</div>
	<c:if test="${not empty fn:trim(contentList)}">
	<div>
		<c:forEach var="content" items="${contentList}">
			<tiles:insertAttribute value="${content}" />
		</c:forEach>	
	</div>
	</c:if>
</div>
<div>
	<tiles:insertAttribute name="ftr"/>
</div >	
</body>
</html>