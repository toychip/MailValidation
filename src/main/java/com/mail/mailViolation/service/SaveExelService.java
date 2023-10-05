package com.mail.mailViolation.service;

import com.mail.mailViolation.dto.dao.MailResultDao;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class SaveExelService {
    public ByteArrayInputStream createExcelFile(List<MailResultDao> dataList) throws IOException {
        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            System.out.println("--------------------------------//////////////////////-------------");
            Sheet sheet = workbook.createSheet("Mail Results");

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
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

            // 데이터 삽입
//            for (int i = 0; i < dataList.size(); i++) {
            for (int i = 0; i < 3; i++) {
                MailResultDao data = dataList.get(i);
                Row row = sheet.createRow(i + 1);

                System.out.println("i = " + i);
                row.createCell(0).setCellValue(i + 1);

                String docNumber = data.getDocNumber();
                row.createCell(1).setCellValue(docNumber);
                System.out.println("----- docNumber = " + docNumber);

                String draftsman = data.getDraftsman();
                row.createCell(2).setCellValue(draftsman);
                System.out.println("----- draftsman = " + draftsman);

                String dept = data.getDept();
                row.createCell(3).setCellValue(dept);
                System.out.println("----- dept = " + dept);

                String mailTitle = data.getMailTitle();
                row.createCell(4).setCellValue(mailTitle);
                System.out.println("----- mailTitle = " + mailTitle);

                String approvalDateString = data.getApprovalDate().toString();
                row.createCell(5).setCellValue(approvalDateString);
                System.out.println("----- approvalDateString = " + approvalDateString);

                String reference = data.getReference();
                row.createCell(6).setCellValue(reference);
                System.out.println("----- reference = " + reference);

                String blockCause = data.getBlockCause();
                row.createCell(7).setCellValue(blockCause);
                System.out.println("----- blockCause = " + blockCause);

                String lastApprover = data.getLastApprover();
                row.createCell(8).setCellValue(lastApprover);
                System.out.println("----- lastApprover = " + lastApprover);

                String result = data.getResult();
                row.createCell(9).setCellValue(result);
                System.out.println("----- result = " + result);


            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
