<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <!-- 한글 인코딩 설정 -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <!-- JSTL 라이브러리 추가 -->

<!DOCTYPE html>
<html>
<head>
    <title>Valid Result 🌟</title>
    <link rel="stylesheet" href="/css/default.css" />
    <style>
        /* 날짜 검색 부분의 크기를 더 크게 설정 */
        #searchInfo h3, #excelDownloadForm button, .container-fluid h1 {
            font-size: 1.5em;
        }

        .table td {
            /* white-space: nowrap; 기존 설정 제거 */
            /* overflow: hidden; 기존 설정 제거 */
            /* text-overflow: ellipsis; 기존 설정 제거 */
            position: relative; /* 툴팁의 위치 기준 설정 */
        }

        /* 툴팁 스타일 */
        .tooltip {
            display: none;
            position: absolute;
            z-index: 1000;
            background-color: #f5f5f5;
            border: 1px solid #ccc;
            padding: 5px;
            max-width: 200px;
            word-wrap: break-word;
            left: 100%;
            white-space: normal;
        }

        /* 마우스 오버시 툴팁 표시 */
        .table td:hover .tooltip {
            display: block;
        }

        .table th:nth-child(8), .table td:nth-child(8) {
            font-size: 0.8em;
            max-width: 600px; /* 참조 열의 최대 너비 설정 */
            /* white-space: nowrap; 이미 적용됨 */
        }

        .table th:nth-child(5), .table td:nth-child(5) {
            font-size: 0.8em;
            max-width: 400px; /* 참조 열의 최대 너비 설정 */
            /* white-space: nowrap; 이미 적용됨 */
        }

        .table th:nth-child(6), .table td:nth-child(6) {
            font-size: 0.8em;
            max-width: 400px; /* 참조 열의 최대 너비 설정 */
            /* white-space: nowrap; 이미 적용됨 */
        }

        .table th:nth-child(3), .table td:nth-child(3) {
            font-size: 1.1em;
            min-width: 80px; /* 참조 열의 최대 너비 설정 */
            /* white-space: nowrap; 이미 적용됨 */
        }
        .table th:nth-child(7), .table td:nth-child(7) {
            font-size: 1.0em;
            min-width: 150px; /* 참조 열의 최대 너비 설정 */
            /* white-space: nowrap; 이미 적용됨 */
        }

        .table th:nth-child(10), .table td:nth-child(10) {
            font-size: 1.1em;
            min-width: 80px; /* 참조 열의 최대 너비 설정 */
            /* white-space: nowrap; 이미 적용됨 */
        }

        .table th:nth-child(11), .table td:nth-child(11) {
            font-size: 1.1em;
            min-width: 80px; /* 참조 열의 최대 너비 설정 */
            /* white-space: nowrap; 이미 적용됨 */
        }
        /* 홀수 및 짝수 행 배경색 설정 */
        .table tr:nth-child(odd) {
            background-color: #ffffff;
        }
        .table tr:nth-child(even) {
            background-color: #f5f5f5;
        }

        /* 테이블 헤더 스타일 */
        .table thead th {
            background-color: #5b5b5b;
        }
        /* 예시 코드에서 가져온 스타일 */


        .title-block.sub-title-block {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .sub-button {
            display: flex;
            justify-content: space-around;
            width: 100%;
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

    </style>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div id="searchInfo" class="text-center mb-4">
    <c:if test="${not empty fromYear and not empty fromMonth and not empty toYear and not empty toMonth}">
        <h3>${fromYear}년 ${fromMonth}월 ~ ${toYear}년 ${toMonth}월 검색 결과</h3>
    </c:if>
</div>
<br>
<br>
<div class="container-fluid mt-5">
    <form id="excelDownloadForm" action="/downloadExcel" method="post" target="_blank">
        <!-- Hidden field to store the JSON string -->
        <input type="hidden" id="hiddenField" name="conditionXListJson">
        <button type="submit" class="btn btn-primary">엑셀 다운로드 📥</button>
    </form>
    <br>
    <ul class="table-block">
        <li class="table__head">
            <table class="table table-responsive" >
                <thead>
                <tr>
                    <th scope="col">NO</th>
                    <th scope="col">문서 번호</th>
                    <th scope="col">기안자</th>
                    <th scope="col">부서</th>
                    <th scope="col">품의 문서 제목</th>
                    <th scope="col">메일 제목</th>
                    <th scope="col">결재일</th>
                    <th scope="col">수신처</th>
                    <th scope="col">👁참조</th>
                    <th scope="col">차단 사유</th>
                    <th scope="col">최종 결재자</th>
                    <th scope="col">적격 여부</th>
                    <th scope="col">부적격사유</th>
                </tr>
                </thead>
                <tbody id="dynamicTbody">
                <!-- 동적으로 생성될 테이블 로우 -->
                </tbody>
            </table>
        </li>
    </ul>
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
            case 'C': return '기안자: 일반사원 <br> 결재: 팀장 <br> 참조: 보직자 참조 X';
            case 'D': return '기안자: 일반사원 <br> 결재: 팀장, 실장, 본부장 중 아무도 안받음';
            case 'E': return '기안자: 팀장 <br> 결재: 실장, 본부장 중 아무도 안받음';
            case 'F': return '기안자: 실장 <br> 결재: 본부장이 안했음';
            case 'G': return '기안자: 일반 사원 <br> 결재: 경영지원실 팀장, 실장, DB관리자 중 한 명에게 받음 <br> 참조: 본인 부서의 보직자(실장 or 본부장) 참조안함';
            case 'H': return '기안자: 팀장 <br> 결재: 경영지원실 팀장, 실장, DB관리자 중 한 명에게 받음 <br> 참조: 본인 부서의 보직자(실장 or 본부장) 참조안함';
            case 'I': return '기안자: 실장 <br> 결재: 경영지원실 팀장, 실장, DB관리자 중 한 명에게 받음 <br> 참조: 본부장을 참조 안함';
            // case 'J': return '여유 공백';
            case 'T': return '현재 퇴사 or 휴직으로 검증 불가';
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
            tbodyHtml += "<td>" + (item.title || '' ) + "</td>";
            tbodyHtml += "<td>" + item.mailTitle + "</td>";

            const formattedDate = formatDate(item.approvalDate);
            tbodyHtml += "<td>" + formattedDate + "</td>";

            tbodyHtml += "<td>" + (item.recipient || '') + "</td>";
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
