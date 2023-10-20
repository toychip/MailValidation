package nice.mailViolation.service;

import nice.mailViolation.dto.MailResultDto;
import nice.mailViolation.dto.ReasonIneligibility;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SaveExelService {

    public void exportToExcel(HttpServletResponse response, List<MailResultDto> conditionXList) throws IOException {

        Collections.sort(conditionXList, new Comparator<MailResultDto>() {
            @Override
            public int compare(MailResultDto m1, MailResultDto m2) {
                return m1.getApprovalDate().compareTo(m2.getApprovalDate());
            }
        });

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Mail Results");
        Row headerRow = sheet.createRow(0);

        // Create header cells
        headerRow.createCell(0).setCellValue("순서");
        headerRow.createCell(1).setCellValue("문서 번호");
        headerRow.createCell(2).setCellValue("기안");
        headerRow.createCell(3).setCellValue("부서");
        headerRow.createCell(4).setCellValue("제목");
        headerRow.createCell(5).setCellValue("문서 제목");
        headerRow.createCell(6).setCellValue("결재일");
        headerRow.createCell(7).setCellValue("참조");
        headerRow.createCell(8).setCellValue("차단 사유");
        headerRow.createCell(9).setCellValue("최종 결재자");
        headerRow.createCell(10).setCellValue("적격 여부");
        headerRow.createCell(11).setCellValue("부적격 사유");

        // rows
        for (int i = 0; i < conditionXList.size(); i++) {
            MailResultDto data = conditionXList.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(data.getDocNumber());
            row.createCell(2).setCellValue(data.getDraftsman());
            row.createCell(3).setCellValue(data.getDept());
            row.createCell(4).setCellValue(data.getTitle());
            row.createCell(5).setCellValue(data.getMailTitle());
            row.createCell(6).setCellValue(data.getApprovalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            row.createCell(7).setCellValue(data.getReference());
            row.createCell(8).setCellValue(data.getBlockCause());
            row.createCell(9).setCellValue(data.getLastApprover());
            row.createCell(10).setCellValue(data.getResult());
            row.createCell(11).setCellValue(getReasonInKorean(data.getReasonIneligibility()));
        }

        // 응답 properties 설정
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=mail_results.xlsx";
        response.setHeader(headerKey, headerValue);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        // workbook 쓰고 닫기
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public String getReasonInKorean(ReasonIneligibility reason) {
//        if (reason == ReasonIneligibility.A) {
//            return "적격";
//        }
//        if (reason == ReasonIneligibility.B) {
//            return "그룹웨어관리 테스트용";
//        }
        if (reason == ReasonIneligibility.C) {
            return "기안자: 일반 사원 | 결재: 팀장 | 참조: 보직좌(실장 or 본부장)를 참조 안함";
        }
        if (reason == ReasonIneligibility.D) {
            return "기안자: 일반 사원 | 결재: 팀장, 실장, 본부장 아무에게도 결재를 받지 않음";
        }
        if (reason == ReasonIneligibility.E) {
            return "기안자: 팀장 | 결재: 실장 or 본부장 중 아무에게도 결재를 받지 않음";
        }
        if (reason == ReasonIneligibility.F) {
            return "기안자: 실장 | 결재: 본부장에게 결재를 받지 않음";
        }
        if (reason == ReasonIneligibility.G) {
            return "기안자: 일반사원 | 결재: 경영지원실 팀장, 실장, DB관리자 중 한 명에게 받음 | 참조: 참조: 본인 부서의 보직좌를 참조하지 않음";
        }
        if (reason == ReasonIneligibility.H) {
            return "기안자: 팀장 | 결재: 경영지원실 팀장, 실장, DB관리자 중 한 명에게 결재를 받음 | 참조:본인 부서의 보직좌를 참조하지 않음";
        }
        if (reason == ReasonIneligibility.I) {
            return "기안자: 실장 | 결재: 경영지원실 팀장, 실장, DB관리자 중 한 명에게 결재를 받음 | 참조: 본부장을 참조하지 않음";
        }
        if (reason == ReasonIneligibility.T) {
            return "퇴사나 휴직";
        }

        return "";  // 올 수 없는 경우
    }

}
