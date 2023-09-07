package com.mail.mailViolation.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final InsertService insertService;

	public List<MailResultDao> processExcelFile(MultipartFile file){


        InputStream inputStream = null;
        List<MailResultDao> mailResultDaoList = new ArrayList<>();
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

            // 4번째 행부터 시작.
//            for (int i = 3; i <= 10; i++) {
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
//                System.out.println("\n\n");
//            	log.info("------------------------------현재 i: " + i);
                Row currentRow = sheet.getRow(i);

                if (currentRow == null) {
                    continue;   // 행이 비어 있으면 건너뛰기
                }

                ApprovalMailRequest approvalMailRequest = initService.createMailDao(currentRow, year);
                EmployeeDao validOverLap = initService.getEmp(approvalMailRequest.getDraftsman());
                Integer validOverLapDeptId = validOverLap.getDeptId();

                String condition;
                // 메일 테스트의 경우로, 부서가 그룹웨어관리가 포함될 경우
                if (approvalMailRequest.getDept().contains("그룹웨어관리")) {
                    condition = "T";
                } else {
                    condition = initService.checkApprovalCondition(approvalMailRequest, validOverLapDeptId);
                }
//                System.out.println("------------------------ 결재자 적격 = " + condition);
//                if (condition.equals("O")) {
//                    System.out.println("보직좌가 포함됨.");
//                }

                mailResultDaoList.add(
                        MailResultDao.builder()
                        .docNumber(approvalMailRequest.getDocNumber())	// 문서 번호
                        .draftsman(approvalMailRequest.getDraftsman())	// 기안자
                        .dept(approvalMailRequest.getDept())	// 소속부서
                        .title(approvalMailRequest.getTitle())	// 제목
                        .approvalDate(approvalMailRequest.getApprovalDate())	// 결재일
                        .mailTitle(approvalMailRequest.getMailTitle())	// 메일 제목
                        .recipient(approvalMailRequest.getRecipient())	// 받는 사람
                        .reference(approvalMailRequest.getReference())	// 참조
                        .blockCause(approvalMailRequest.getBlockCause())	// 차단사유
                        .lastApprover(approvalMailRequest.getLastApprover())	// 최종 결재
                        .result(condition)		// 적격 여부 적격: O, 부적격: X, 테스트: T
                        .build()
                );

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

        List<Integer> noBossDepartments = initService.getNoBossDepartments();
        for (Integer dept : noBossDepartments) {
            System.out.println("----------------- 보직좌가 없는 부서: " +dept);
        }
		return mailResultDaoList;
	}

}
