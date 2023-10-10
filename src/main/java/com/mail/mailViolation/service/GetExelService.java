package com.mail.mailViolation.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.mail.mailViolation.dto.dao.EmployeeDao;
import com.mail.mailViolation.dto.dto.ApprovalMailDto;
import com.mail.mailViolation.dto.dto.ReturnDto;
import com.mail.mailViolation.exception.ExelUploadException;
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

import com.mail.mailViolation.dto.dao.MailResultDao;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetExelService {

	private final InitService initService;

    // 엑셀 파일 처리 메서드
	public ReturnDto processExcelFile(MultipartFile file){


        InputStream inputStream = null;
        List<MailResultDao> conditionXList = new ArrayList<>();
        List<MailResultDao> conditionOList = new ArrayList<>();

//		log.info("-------------------------엑셀 처리 전 로그");
		try {

            // log.info("------------------------- 엑셀 파일을 읽기 위한 스트림 생성 ");
            inputStream = file.getInputStream();
            // Apache POI 라이브러리를 사용하여 Workbook 객체 생성
            Workbook workbook = WorkbookFactory.create(inputStream);
            // 첫 번째 시트
            Sheet sheet = workbook.getSheetAt(0);

            // 년도 추출
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(0);
            String strDate = cell.toString();
            String year = strDate.substring(7, 11);

            // 엑셀 파일의 4번째 행부터 값 추출.
//            for (int i = 3; i <= 100; i++) {
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
//                log.info("\n\n");
//            	log.info("------------------------------현재 i: " + i);
                Row currentRow = sheet.getRow(i);

                if (currentRow == null) {
                    continue;   // 행이 비어 있으면 건너뛰기
                }

                // 엑셀 행을 DTO로 변환
                ApprovalMailDto approvalMailDto = initService.createMailDao(currentRow, year);

                // DTO를 기반으로 직원 정보 가져옴
                EmployeeDao findEmp = initService.getEmp(approvalMailDto.getDraftsman());
                Integer empDeptId = findEmp.getDeptId();

                String condition;
                String title = approvalMailDto.getTitle();

                String apprReferYn = findEmp.getApprReferYn();

                if (approvalMailDto.getDept().contains("그룹웨어관리")) {
                    condition = "T";
                }

                if (apprReferYn == "Y") {
                    condition = initService.validateForMember(approvalMailDto, empDeptId);
                }

                if (apprReferYn == "T") {
                    condition = initService.validateForTBoss(approvalMailDto, empDeptId);
                }

                if (apprReferYn == "S") {
                    condition = initService.validateForSBoss(approvalMailDto, empDeptId);
                }

                // 결재 조건 확인
                // 메일 테스트의 경우로, 부서가 그룹웨어관리가 포함될 경우
                if (approvalMailDto.getDept().contains("그룹웨어관리")) {
                    condition = "T";
                } else {

                    // initService의 검사 로직탐
                    condition = initService.checkApprovalCondition(approvalMailDto, empDeptId);

                    // condition이 "X"일 경우, 부적격 리스트에 추가
                    if (condition == "X") {
                        conditionXList.add(
                                MailResultDao.builder()
                                        .docNumber(approvalMailDto.getDocNumber())	// 문서 번호
                                        .draftsman(approvalMailDto.getDraftsman())	// 기안자
                                        .dept(approvalMailDto.getDept())	// 소속부서
                                        .deptId(empDeptId)
                                        .title(title)	// 제목
                                        .approvalDate(approvalMailDto.getApprovalDate())	// 결재일
                                        .mailTitle(approvalMailDto.getMailTitle())	// 메일 제목
                                        .recipient(approvalMailDto.getRecipient())	// 받는 사람
                                        .reference(approvalMailDto.getReference())	// 참조
                                        .blockCause(approvalMailDto.getBlockCause())	// 차단사유
                                        .lastApprover(approvalMailDto.getLastApprover())	// 최종 결재
                                        .result(condition)		// 적격 여부 적격: O, 부적격: X, 테스트: T
                                        .build()
                        );

//                        ReturnDto.ReturnDtoBuilder returnDtoBuilder = ReturnDto.builder()
//                                .conditionXList(conditionXList);

                    }
                }
                // condition이 "O" 또는 "T"일 경우, 적격 리스트에 추가
                conditionOList.add(
                        MailResultDao.builder()
                        .docNumber(approvalMailDto.getDocNumber())	// 문서 번호
                        .draftsman(approvalMailDto.getDraftsman())	// 기안자
                        .dept(approvalMailDto.getDept())	// 소속부서
                        .deptId(empDeptId)
                        .title(title)	// 제목
                        .approvalDate(approvalMailDto.getApprovalDate())	// 결재일
                        .mailTitle(approvalMailDto.getMailTitle())	// 메일 제목
                        .recipient(approvalMailDto.getRecipient())	// 받는 사람
                        .reference(approvalMailDto.getReference())	// 참조
                        .blockCause(approvalMailDto.getBlockCause())	// 차단사유
                        .lastApprover(approvalMailDto.getLastApprover())	// 최종 결재
                        .result(condition)		// 적격 여부 적격: O, 부적격: X, 테스트: T
                        .build()
                );
            }

            workbook.close();
        } catch (Exception e) {

            throw new ExelUploadException();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();     // 스트림 닫기
                } catch (IOException e) {
                    throw new ExelUploadException();
                }
            }
        }

        // 최종 결과 반환
		return ReturnDto.builder()
                .conditionXList(conditionXList.isEmpty() ? null:conditionXList)
                .conditionOList(conditionOList)
                .build();
	}

}
