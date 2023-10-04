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
    public ByteArrayInputStream createExcelFile(String dataList) throws IOException {
        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
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

//            // 데이터 삽입
//            for (int i = 0; i < dataList.size(); i++) {
//                MailResultDao data = dataList.get(i);
//                Row row = sheet.createRow(i + 1);
//
//                row.createCell(0).setCellValue(i + 1);
//                row.createCell(1).setCellValue(data.getDocNumber());
//                row.createCell(2).setCellValue(data.getDraftsman());
//                row.createCell(3).setCellValue(data.getDept());
//                row.createCell(4).setCellValue(data.getMailTitle());
//                row.createCell(5).setCellValue(data.getApprovalDate().toString());
//                row.createCell(6).setCellValue(data.getReference());
//                row.createCell(7).setCellValue(data.getBlockCause());
//                row.createCell(8).setCellValue(data.getLastApprover());
//                row.createCell(9).setCellValue(data.getResult());
//            }
            return new ByteArrayInputStream(out.toByteArray());

        }
    }
}
