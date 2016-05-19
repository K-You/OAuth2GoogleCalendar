<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
<meta charset="UTF-8">
<title>Google Events</title>
</head>
<body>
	<table>
		<c:forEach var="event" items="${events}">
			<tr>
				<td>${event.summary}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>