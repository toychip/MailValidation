<%@ page import="java.util.ArrayList" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>Excel Upload 📊</title>
	<link rel="stylesheet" href="/css/default.css" />
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

		.tab-ul {
			border: 1px solid;
			width: 120px;
			height: 35px;
			float: left;
			background-color: #f5f7fa;
		}

		.tab-button {
			width: 100px;
			height: 32px;
			background-color: #f5f7fa;
		}
		.title-block {
			margin-bottom: 20px; /* 공백 크기 조절 */
		}

		h1 {
			margin-top: 20px; /* 상단 마진 조절 */
			font-size: 24px; /* 글꼴 크기 증가 */
		}
		.btn-lg {
			font-size: 2em; /* 버튼의 글꼴 크기를 h1 태그와 동일하게 설정 */
			padding: 10px 20px; /* 버튼의 패딩을 조정하여 텍스트에 맞게 조정 */
		}
		.form-group {
			display: flex;
			align-items: center;
			justify-content: center; /* 가운데 정렬 */
			flex-wrap: wrap; /* 요소들이 충분한 공간이 없을 때 줄바꿈 */
		}

		.form-group h1, .form-group input, .form-group button {
			margin: 0 15px; /* 요소들 사이에 양쪽으로 마진을 적용 */
		}
		/* 가운데 정렬을 위한 스타일 */
		.title-block {
			margin-bottom: 50px; /* 공간 추가 */
		}

		.centered {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
		}

		.centered > * {
			margin: 10px 0; /* 모든 자식 요소에 상하 여백 추가 */
		}

		.file-upload {
			margin-bottom: 50px; /* 파일 업로드 부분과 Search By Date 사이의 공간 조정 */
		}

		/* 버튼 크기 조정 */
		.btn-lg {
			padding: 5px 10px; /* 현재 크기의 절반으로 조정 */
			font-size: 0.875em; /* 글꼴 크기 조정 */
		}

		/* 타이틀과 파일 업로드 사이의 공간 조정 */
		.title-block {
			margin-bottom: 50px; /* Search By Date와 동일한 공간 추가 */
		}
	</style>
</head>
<body>
<div id="wrap">
	<form method="GET" action="/logout">
		<input style=" float: left;" type="image" src="<c:url value='/images/top_logout.gif' />" title="로그아웃" style="width:60px; height:20px;"/>

	</form>
	<!-- 페이지 이동 탭 -->
	<div style="float: right; text-align: center">
		<ul class="tab-ul" id="submission" style="border: 1px solid; width: 120px; height: 35px; float: left; background-color: #f5f7fa;">
			<button class="tab-button" id="submissionBtn"
					onClick="location.href='/bohoManagement'">제출 진행현황</button>
		</ul>
		<ul class="tab-ul" id="schedule" style="border: 1px solid; width: 120px; height: 35px; float: left; background-color: #f5f7fa;">
			<button class="tab-button" id="scheduleBtn"
					onClick="location.href='/scheduleManagementPage'">일정 관리</button>
		</ul>
		<ul class="tab-ul" id="survey" style="border: 1px solid; width: 120px; height: 35px; float: left; background-color: #f5f7fa;">
			<button class="tab-button" id="surveyBtn"
					onClick="location.href='/surveyManagementPage'">설문항목 관리</button>
		</ul>
		<ul class="tab-ul" id="email" style="border: 1px solid; width: 120px; height: 35px; float: left; background-color: #f5f7fa;">
			<button class="tab-button" id="emailBtn"
					onClick = "location.href='/upload'">메일승인 점검</button>
		</ul>
	</div>
	<%
		// 서버에서 설정한 에러 메시지를 가져옵니다.
		ArrayList<String> errors = (ArrayList<String>) request.getAttribute("errors");
		String errorMessages = errors != null ? String.join("\n", errors) : "";
	%>

	<div id="errors" style="display:none;"><%= errorMessages %></div>


	<%--<div id="errors" style="display:none;"><%= errors %></div>--%>
	<div class="title-block top-title-block">
		<strong>생활보안점검 메일 승인 관리 💌 </strong> <span class="line"></span>
	</div>
	<br>
	<br>
	<br>

	<div class="centered">
		<h1>⬇️엑셀을 업로드하세요 📁</h1>
		<br>

		<div class="file-upload">
			<form id="uploadForm" action="/upload" method="post" enctype="multipart/form-data">
				<input type="file" id="file" name="file" class="form-control" accept=".xls,.xlsx,.xlsm,.xlsb">
				<button type="submit" class="btn btn-primary btn-lg">검사하기 🔍</button>
			</form>
		</div>

		<!-- 추가된 검색창 부분 -->
		<h1>Search By Date 📅</h1>
		<form id="searchForm">
			<div class="form-group">
				<h1>From:</h1>
				<input type="month" id="currentMonth" name="startMonth">

				<h1>To:</h1>
				<input type="month" id="nextMonth" name="finishMonth">

				<button type="submit" class="btn btn-secondary btn-lg">Search 🔍</button>
			</div>
		</form>
	</div>
	<!-- 로딩 스피너 -->
	<div id="loadingSpinner">
		<div class="spinner-content">
			<img src='https://i.stack.imgur.com/FhHRx.gif' alt="Loading...">
			<p>검사중... 잠시만 기다려주세요</p>  <!-- 이 부분이 추가된 문구입니다. -->
		</div>
	</div>
</div>
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script type="text/javascript">

	window.onload = function(){

		$('#email').css('background-color', 'rgb(170, 218, 223)');
		$('#emailBtn').css('background-color', 'rgb(170, 218, 223)');
	}


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
