<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <!-- 한글 인코딩 설정 -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <!-- JSTL 라이브러리 추가 -->

<!DOCTYPE html>
<html>
<head>
    <title>Valid Result 🌟</title>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* ... (CSS는 그대로 사용 가능하므로 생략) ... */
        .limit-reference {
            max-width: 200px;  /* 최대 너비 설정 */
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
            <th>👤 기안자</th>
            <th>🏢 부서</th>
            <th>📄 문서 제목</th>
            <th>📅 결재일</th>
            <th>👁️ 참조</th>
            <th>🚫 차단 사유</th>
            <th>👑 최종 결재자</th>
            <th>✔️ 적격 여부</th>
            <th>🚫 부적격사유</th>
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

    function getReasonInKorean(reason) {
        switch (reason) {
            // case 'A': return '적격';
            // case 'B': return '그룹웨어관리 테스트용';
            case 'C': return '일반 사원이고, 팀장한테 결재받았지만 보직좌(실장 혹은 본부장)에게 결재를 받지 않음';
            case 'D': return '일반 사원이고, 팀장 실장 본부장 중 아무에게도 결재를 받지 않음';
            case 'E': return '팀장이고, 실장 혹은 본부장 중 아무에게도 결재를 받지 않음';
            case 'F': return '실장이고, 본부장에게 결재를 받지 않음';
            case 'G': return '일반 사원이고, 경영지원실 팀장, 실장, DB관리자 중 한 명에게 결재를 받았지만, 본인 부서의 보직좌를 참조하지 않음';
            case 'H': return '팀장 이고, 경영지원실 팀장, 실장, DB관리자 중 한 명에게 결재를 받았지만, 본인 부서의 보직좌를 참조하지 않음';
            case 'I': return '실장 이고, 경영지원실 팀장, 실장, DB관리자 중 한 명에게 결재를 받았지만, 본부장을 참조하지 않음';
            // case 'J': return '여유 공백';
            case 'T': return '현재 퇴사나 휴직으로 검증 불가';
            default: return '알 수 없음';
        }
    }


    // 이 부분은 변경할 필요가 없습니다.
    var jsonTemp = ${conditionXList};

    // 새로운 객체를 생성하여 undefined를 null로 변환
    var jsonResult = replaceUndefinedOrNull(jsonTemp);

    // Ajax 호출 부분
    $("#excelDownloadForm").submit(function(event) {
        event.preventDefault();

        $.ajax({
            url: '/downloadExcel',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(jsonResult),
            xhrFields: {
                responseType: 'blob'
            },
            success: function(response, status, xhr) {
                var blob = new Blob([response], { type: 'application/vnd.ms-excel' });
                var link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = 'mail_results.xlsx';
                link.click();
                window.URL.revokeObjectURL(link.href);
            },
            error: function(xhr, status, error) {
                alert('Excel download failed.');
            }
        });
    });

    function formatDate(dateString) {
        console.log("Input dateString: ", dateString);  // 이 부분 추가
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1을 해줍니다.
        const day = date.getDate();
        const hour = date.getHours();
        const minute = date.getMinutes();

        let temp =  year+"년 "+ month + "월 " + day + "일 " + hour + "시 " + minute + "분";
        return temp;
    }



    $(document).ready(function() {
        // 서버에서 전달받은 JSON 문자열을 JavaScript 객체로 변환
        <%--var conditionXList = JSON.parse('<c:out value="${conditionXList}" />');--%>

        // 날짜를 오름차순으로 정렬
        jsonTemp.sort(function(a, b) {
            const dateA = new Date(a.approvalDate);
            const dateB = new Date(b.approvalDate);
            return dateA - dateB;
        });


        // 동적으로 테이블 로우 생성
        var tbodyHtml = "";
        jsonTemp.forEach(function(item, index) {
            tbodyHtml += "<tr>";
            tbodyHtml += "<td>" + (index + 1) + "</td>";
            tbodyHtml += "<td>" + item.docNumber + "</td>";
            tbodyHtml += "<td>" + item.draftsman + "</td>";
            tbodyHtml += "<td>" + item.dept + "</td>";
            tbodyHtml += "<td class='limit-reference'>" + (item.title || '' ) + "</td>";

            const formattedDate = formatDate(item.approvalDate);
            tbodyHtml += "<td>" + formattedDate + "</td>";

            tbodyHtml += "<td>" + (item.reference || '') + "</td>";
            tbodyHtml += "<td>" + item.blockCause + "</td>";
            tbodyHtml += "<td>" + item.lastApprover + "</td>";
            tbodyHtml += "<td>" + item.result + "</td>";
            tbodyHtml += "<td>" + getReasonInKorean(item.reasonIneligibility) + "</td>";
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
