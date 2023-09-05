package com.mail.mailViolation.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.mail.mailViolation.dto.EmployeeDao;
import com.mail.mailViolation.dto.request.ApprovalMailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import com.mail.mailViolation.dto.MailResultDao;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExelService {

	private final InitService initService;
	
	public List<MailResultDao> processExcelFile(MultipartFile file){

		
        InputStream inputStream = null;
		log.info("-------------------------엑셀 처리 전 로그");
		try {

            // ------------------------- 엑셀 처리 로직 시작
            inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
            // ------------------------- 엑셀 처리 로직 끝

            // 년도 추출
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(0);
            String strDate = cell.toString();
            String year = strDate.substring(7, 11);
            
    		log.info("------------------------- cell 처리 전 로그");
            log.info("-------------ExelService---------------"+ year + "class() = "+year.getClass());
            
            
            // 4번째 행부터 시작.
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
            	log.info("------------------------------현재 i: " + i);
                log.info("------------------------------sheet.getLastRowNum() = " + sheet.getLastRowNum());
                Row currentRow = sheet.getRow(i);

                if (currentRow == null) {
                    continue;   // 행이 비어 있으면 건너뛰기
                }

                ApprovalMailRequest approvalMailRequest = initService.createMailDao(currentRow, year);
                System.out.println("------------------------approvalMailRequest.getDraftsman() = " + approvalMailRequest.getDraftsman());
                EmployeeDao validOverLap = initService.getEmp(approvalMailRequest.getDraftsman());
                System.out.println("------------------------validOverLap.getEmpName() = " + validOverLap.getEmpName());
                Long validOverLapDeptId = validOverLap.getDeptId();
                System.out.println("------------------------validOverLapDeptId = " + validOverLapDeptId);
                boolean condition = initService.checkApprovalCondition(approvalMailRequest, validOverLapDeptId);
                System.out.println("------------------------condition = " + condition);
                if (condition) {
                    System.out.println("보직좌가 포함됨.");
                }

//                 db 저장 로직
            }
            workbook.close();
        } catch (Exception e) {
            // 파일 처리 중 오류 발생 시 오류 추가
            FieldError error = new FieldError("file", "file", "파일 처리 중 오류가 발생했습니다: " + e.getMessage());
//            bindingResult.addError(error);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();     // 스트림 닫기
                } catch (IOException e) {
                    FieldError error = new FieldError("file", "file", "파일 스트림 닫기 중 오류가 발생했습니다: " + e.getMessage());
//                    bindingResult.addError(error);
                }
            }
        }
		return null;
	}
	
}
