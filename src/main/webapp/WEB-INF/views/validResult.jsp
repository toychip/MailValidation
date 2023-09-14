<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <!-- 한글 인코딩 설정 -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <!-- JSTL 라이브러리 추가 -->

<!DOCTYPE html>
<html>
<head>
    <title>Valid Result 🌟</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* ... (CSS는 그대로 사용 가능하므로 생략) ... */
    </style>
</head>
<body>
<div class="container-fluid mt-5">
    <form action="/downloadExcel" method="post" target="_blank">
        <button type="submit" class="btn btn-primary">📥 엑셀 다운로드 📥</button>
    </form>
    <h1 class="text-center">✨✨ 검증 결과 ✨✨</h1>
    <table class="table table-responsive">
        <thead>
        <tr>
            <th>🔢 순서</th>
            <th>📑 문서 번호</th>
            <th>👤 기안</th>
            <th>🏢 부서</th>
            <th>📄 문서 제목</th>
            <th>📅 결재일</th>
            <th>👁️ 참조</th>
            <th>🚫 차단 사유</th>
            <th>👑 최종 결재자</th>
            <th>✔️ 적격 여부</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${conditionXList}" var="mail" varStatus="iterStat">
            <tr>
                <td>${iterStat.index + 1}</td>
                <td>${mail.docNumber != null ? mail.docNumber : ' '}</td>
                <td>${mail.draftsman != null ? mail.draftsman : ' '}</td>
                <td>${mail.dept != null ? mail.dept : ' '}</td>
                <td>${mail.mailTitle != null ? mail.mailTitle : ' '}</td>
                <td>${mail.approvalDate != null ? mail.approvalDate : ' '}</td>
                <td>${mail.reference != null ? mail.reference : ' '}</td>
                <td>${mail.blockCause != null ? mail.blockCause : ' '}</td>
                <td>${mail.lastApprover != null ? mail.lastApprover : ' '}</td>
                <td>${mail.result != null ? mail.result : ' '}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
