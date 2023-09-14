<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>Excel Upload 📊</title>
	<link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
	<!-- 여기에 추가 스타일 -->
</head>
<body>

<%
	// 서버에서 설정한 에러 메시지를 가져옵니다.
	// 이 부분은 컨트롤러에서 `request.setAttribute("errors", errors);` 형태로 설정해야 합니다.
	String errors = (String) request.getAttribute("errors");
%>

<div id="errors" style="display:none;"><%= errors %></div>

<div class="container text-center">
	<h1>Excel File Upload 📁</h1>
	<form id="uploadForm" action="/upload" method="post" enctype="multipart/form-data">
		<div class="form-group">
			<label for="file">Choose Excel file to upload:</label>
			<input type="file" id="file" name="file" class="form-control">
		</div>
		<button type="submit" class="btn btn-primary btn-lg">검사하기 🔍</button>
	</form>
</div>

<!-- 로딩 스피너 -->
<div id="loadingSpinner">
	<div class="spinner-content">
		<img src='https://i.stack.imgur.com/FhHRx.gif' alt="Loading...">
		<p>검사중... 잠시만 기다려주세요</p>
	</div>
</div>

<!-- 자바스크립트와 추가 스크립트 -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<script type="text/javascript">
	document.getElementById("uploadForm").addEventListener("submit", function() {
		document.getElementById("loadingSpinner").style.display = "block";
	});

	var uploadErrors = '<%= errors %>';

	if (uploadErrors && uploadErrors.length > 0) {
		alert("파일 업로드 에러:\n" + uploadErrors);
	}
</script>

</body>
</html>
