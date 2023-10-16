package com.mail.mailViolation.service;

import com.mail.mailViolation.dto.MailResultDto;
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
        headerRow.createCell(4).setCellValue("문서 제목");
        headerRow.createCell(5).setCellValue("결재일");
        headerRow.createCell(6).setCellValue("참조");
        headerRow.createCell(7).setCellValue("차단 사유");
        headerRow.createCell(8).setCellValue("최종 결재자");
        headerRow.createCell(9).setCellValue("적격 여부");

        // rows
        for (int i = 0; i < conditionXList.size(); i++) {
            MailResultDto data = conditionXList.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(data.getDocNumber());
            row.createCell(2).setCellValue(data.getDraftsman());
            row.createCell(3).setCellValue(data.getDept());
            row.createCell(4).setCellValue(data.getTitle());
            row.createCell(5).setCellValue(data.getApprovalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            row.createCell(6).setCellValue(data.getReference());
            row.createCell(7).setCellValue(data.getBlockCause());
            row.createCell(8).setCellValue(data.getLastApprover());
            row.createCell(9).setCellValue(data.getResult());
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
}
