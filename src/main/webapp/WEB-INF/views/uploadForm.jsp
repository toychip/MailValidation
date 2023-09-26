<%@ page import="java.util.ArrayList" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>Excel Upload 📊</title>
	<link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
	<style>
		/* 화면 잠금 처리를 위한 스타일 */
		body.loading {
			overflow: hidden;
			pointer-events: none;
			opacity: 0.4;
		}

		/* 로딩 스피너 스타일 */
		#loadingSpinner {
			display: none;
			position: fixed;
			z-index: 10000;
			top: 0;
			left: 0;
			height: 100%;
			width: 100%;
			background: rgba(255, 255, 255, .8);
		}

		.spinner-content {
			position: fixed;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
			text-align: center;
			z-index: 10001;
		}
	</style>
</head>
<body>

<%
	// 서버에서 설정한 에러 메시지를 가져옵니다.
	ArrayList<String> errors = (ArrayList<String>) request.getAttribute("errors");
	String errorMessages = errors != null ? String.join("\n", errors) : "";
%>

<div id="errors" style="display:none;"><%= errorMessages %></div>


<%--<div id="errors" style="display:none;"><%= errors %></div>--%>

<div class="container text-center">
	<h1>Excel File Upload 📁</h1>
	<form id="uploadForm" action="/upload" method="post" enctype="multipart/form-data">
		<div class="form-group">
			<label for="file">Choose Excel file to upload:</label>
			<input type="file" id="file" name="file" class="form-control">
		</div>
		<button type="submit" class="btn btn-primary btn-lg">검사하기 🔍</button>
	</form>

	<!-- 추가된 검색창 부분 -->
	<h2>Search By Date 📅</h2>
	<form id="searchForm">
		<div class="form-group">
			<label>From:</label>
			<input type="month" id="currentMonth" name="startMonth">
			<label>To:</label>
			<input type="month" id="nextMonth" name="finishMonth">
		</div>
		<button type="submit" class="btn btn-secondary">Search 🔍</button>
	</form>
</div>

<!-- 로딩 스피너 -->
<div id="loadingSpinner">
	<div class="spinner-content">
		<img src='https://i.stack.imgur.com/FhHRx.gif' alt="Loading...">
		<p>검사중... 잠시만 기다려주세요</p>  <!-- 이 부분이 추가된 문구입니다. -->
	</div>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script type="text/javascript">
	document.getElementById("file").addEventListener('change', function() {
		var maxSize = 5 * 1024 * 1024; // 5MB
		if (this.files[0].size > maxSize) {
			alert('파일 크기가 너무 큽니다. 최대 5MB까지 업로드 가능합니다.');
			this.value = "";
		}
	}, false);

	document.getElementById("uploadForm").addEventListener("submit", function() {
		// 로딩 스피너 보이기
		document.getElementById("loadingSpinner").style.display = "block";
		document.body.classList.add("loading");
	});

	// JSP에서 생성한 errorMessages 변수를 사용
	var uploadErrors = '<%= errorMessages %>';

	if (uploadErrors && uploadErrors.length > 0) {
		alert("파일 업로드 에러\n" + uploadErrors);
	}

	// 추가된 검색창 관련 코드
	document.getElementById("searchForm").addEventListener("submit", function(event) {
		event.preventDefault();

		var fromDate = document.getElementById("currentMonth").value || '9999-99';
		var toDate = document.getElementById("nextMonth").value || '9999-99';

		var fromParts = fromDate.split("-");
		var toParts = toDate.split("-");

		var fromYear = parseInt(fromParts[0]) || 9999;
		var fromMonth = parseInt(fromParts[1]) || 99;
		var toYear = parseInt(toParts[0]) || 9999;
		var toMonth = parseInt(toParts[1]) || 99;

		window.location.href = "/getList?fromYear=" + fromYear + "&fromMonth=" + fromMonth + "&toYear=" + toYear + "&toMonth=" + toMonth;
	});
</script>
</body>
</html>
