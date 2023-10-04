<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <!-- 한글 인코딩 설정 -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <!-- JSTL 라이브러리 추가 -->

<!DOCTYPE html>
<html>
<head>
    <title>Valid Result 🌟</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* ... (CSS는 그대로 사용 가능하므로 생략) ... */
        .limit-text {
            max-width: 400px;  /* 최대 너비 설정 */
            overflow: hidden;  /* 넘치는 부분은 숨김 */
            text-overflow: ellipsis;  /* 넘치는 텍스트를 "..."로 표시 */
            white-space: nowrap;  /* 텍스트를 한 줄로 표시 */
        }

        .limit-title {
            max-width: 300px;  /* 최대 너비 설정 */
            overflow: hidden;  /* 넘치는 부분은 숨김 */
            text-overflow: ellipsis;  /* 넘치는 텍스트를 "..."로 표시 */
            white-space: nowrap;  /* 텍스트를 한 줄로 표시 */
        }

        .limit-reason {
            max-width: 300px;  /* 최대 너비 설정 */
            overflow: hidden;  /* 넘치는 부분은 숨김 */
            text-overflow: ellipsis;  /* 넘치는 텍스트를 "..."로 표시 */
            white-space: nowrap;  /* 텍스트를 한 줄로 표시 */
        }

        body {
            background: linear-gradient(90deg, rgba(255, 0, 150, 1) 0%, rgba(0, 204, 255, 1) 100%);
        }

        h1, h3 {
            color: white;
            text-shadow: 2px 2px 4px #000000;
        }

        .btn-primary {
            background: linear-gradient(45deg, #ff0066, #ffcc00);
            border: none;
            box-shadow: 0px 3px 10px rgba(0, 0, 0, 0.2);
        }

        .table {
            background-color: white;
            border-radius: 15px;
            box-shadow: 0px 0px 15px rgba(0, 0, 0, 0.1);
        }

        th {
            background: linear-gradient(45deg, #00ffaa, #ff00cc);
            color: white;
        }

        td {
            color: #333;
        }

        tr:nth-child(even) {
            background-color: #f2f2f2;
        }

        tr:hover {
            background-color: #ddd;
        }
    </style>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div id="searchInfo" class="text-center mb-4">
    <c:if test="${not empty fromYear and not empty fromMonth and not empty toYear and not empty toMonth}">
        <h3>${fromYear}년 ${fromMonth}월부터 ${toYear}년 ${toMonth}월까지의 검색 결과</h3>
    </c:if>
</div>
<div class="container-fluid mt-5">
    <form id="excelDownloadForm" action="/downloadExcel" method="post" target="_blank">
        <!-- Hidden field to store the JSON string -->
        <input type="hidden" id="hiddenField" name="conditionXListJson">
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
        <tbody id="dynamicTbody">
        <!-- 동적으로 생성될 테이블 로우 -->
        </tbody>
    </table>
</div>
<!-- 검색 결과 날짜 정보를 표시하는 섹션 -->

<script type="text/javascript">

    var jsonTemp = ${conditionXList};

    console.log(jsonTemp)

    // undefined 값을 null로 변환
    function replaceUndefinedOrNull(obj) {
        let newObj = Array.isArray(obj) ? [] : {};
        for (let key in obj) {
            if (obj[key] === undefined) {
                newObj[key] = null;
            } else if (obj[key] === Object(obj[key])) {
                newObj[key] = replaceUndefinedOrNull(obj[key]);
            } else {
                newObj[key] = obj[key];
            }
        }
        return newObj;
    }

    // 이 부분은 변경할 필요가 없습니다.
    var jsonTemp = ${conditionXList};

    // 새로운 객체를 생성하여 undefined를 null로 변환
    var jsonResult = replaceUndefinedOrNull(jsonTemp);

    // Ajax 호출 부분
    $("#excelDownloadForm button[type='submit']").click(function(event) {
        event.preventDefault(); // 기본 이벤트(페이지 리로드 등)를 막음

        $.ajax({
            url: '/downloadExcel',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(jsonResult), // JavaScript 객체를 JSON 문자열로 변환
            success: function(response) {
                // 성공적으로 요청이 완료된 후 실행할 코드
            },
            error: function(error) {
                // 요청이 실패한 경우 실행할 코드
            }
        });
    });

    $(document).ready(function() {
        // 서버에서 전달받은 JSON 문자열을 JavaScript 객체로 변환
        <%--var conditionXList = JSON.parse('<c:out value="${conditionXList}" />');--%>

        // 동적으로 테이블 로우 생성
        var tbodyHtml = "";
        jsonTemp.forEach(function(item, index) {
            tbodyHtml += "<tr>";
            tbodyHtml += "<td>" + (index + 1) + "</td>";
            tbodyHtml += "<td>" + item.docNumber + "</td>";
            tbodyHtml += "<td>" + item.draftsman + "</td>";
            tbodyHtml += "<td>" + item.dept + "</td>";
            tbodyHtml += "<td>" + item.title + "</td>";
            tbodyHtml += "<td>" + item.approvalDate + "</td>";
            tbodyHtml += "<td>" + (item.reference || 'N/A') + "</td>";
            tbodyHtml += "<td>" + item.blockCause + "</td>";
            tbodyHtml += "<td>" + item.lastApprover + "</td>";
            tbodyHtml += "<td>" + item.result + "</td>";
            tbodyHtml += "</tr>";
        });

        // 생성된 HTML을 tbody에 추가
        $('#dynamicTbody').html(tbodyHtml);
    });
</script>
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

</body>
</html>
