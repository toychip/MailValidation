package com.mail.mailViolation.service;

import com.mail.mailViolation.dto.dao.EmployeeDao;
import com.mail.mailViolation.dto.dao.MailResultDao;
import com.mail.mailViolation.dto.dto.ApprovalMailDto;
import com.mail.mailViolation.dto.dto.ReturnDto;
import com.mail.mailViolation.exception.ExelUploadException;
import com.mail.mailViolation.mapper.MailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetExelService {

	private final InitService initService;

    private final CheckValidate checkValidate;

    private final MailMapper mapper;

    @Value("${management.support.team.tboss}")
    private String managementTeamTBoss;

    @Value("${management.support.team.sboss}")
    private String managementTeamSBoss;

    @Value("${it.innovation.team.tboss}")
    private String itInnovationTeamTBoss;

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
            for (int i = 3; i <= 100; i++) {
//            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                log.info("\n\n");
            	log.info("------------------------------현재 i: " + i);
                Row currentRow = sheet.getRow(i);

                if (currentRow == null) {
                    continue;   // 행이 비어 있으면 건너뛰기
                }

                // 엑셀 행을 DTO로 변환
                ApprovalMailDto approvalMailDto = initService.createMailDao(currentRow, year);

                // DTO를 기반으로 직원 정보 가져옴
                EmployeeDao findEmp = checkValidate.getEmp(approvalMailDto.getDraftsman());
                Integer empDeptId = findEmp.getDeptId();
                System.out.println("-------------------- empDeptId = " + empDeptId);

                String condition = "X";
                String title = approvalMailDto.getTitle();
                System.out.println("--------------------  title = " + title);

                // 기안자 등급
                String apprReferYn = findEmp.getApprReferYn();
                System.out.println("--------------------  apprReferYn = " + apprReferYn);

                // 결재자
                String lastApprover = approvalMailDto.getLastApprover();
                System.out.println("-------------------- lastApprover = " + lastApprover);

                // 참조
                String referencer = approvalMailDto.getReference();
                System.out.println("-------------------- referencer = " + referencer);

                // 실장
                String sBossEmpName = checkValidate.findSBoss(empDeptId);
                System.out.println("-------------------- 기안자의 실장: = " + sBossEmpName);

                // 본부장
                String bBossEmpName = checkValidate.findBBoss(empDeptId);
                System.out.println("-------------------- 기안자의 본부장: = " + bBossEmpName);

                if (approvalMailDto.getDept().contains("그룹웨어관리")) {
                    condition = "T";
                }

                // 결재를 받으려는 사람이 일반 사원일 경우
                if (apprReferYn == "N") {
                    System.out.println("나는 일반 사원입니다 내 이름은 " + findEmp.getEmpName());
                    condition = checkValidate.basicEmployee(lastApprover, empDeptId, referencer, sBossEmpName, bBossEmpName);
                }

                // 결재를 받으려는 사람이 팀장일 경우
                if (apprReferYn == "T") {
                    System.out.println("나는 팀장입니다 내 이름은 " + findEmp.getEmpName());
                    boolean approvalSBBoss = checkValidate.matchSBBoss(lastApprover, sBossEmpName, bBossEmpName);
                    condition = checkValidate.checkCondition(approvalSBBoss);
                }

                // 결재를 받으려는 사람이 실장일 경우
                if (apprReferYn == "S") {
                    // 본부장이 결재한 경우
                    System.out.println("나는 실장입니다 내 이름은 " + findEmp.getEmpName());
                    boolean isApprovalBBoss = checkValidate.matchBoss(lastApprover, bBossEmpName);
                    condition = checkValidate.checkCondition(isApprovalBBoss);
                }

                // DB 관리자인 it 혁신실 팀장, 결재 관리하는 경영지원실 팀장 및 실장일 경우
                boolean masterApproval = (lastApprover.equals(managementTeamTBoss))
                        || (lastApprover.equals(managementTeamSBoss))
                        || (lastApprover.equals(itInnovationTeamTBoss));

                if (masterApproval) {
                    boolean isReferenceSBBoss = checkValidate.matchSBBoss(referencer, sBossEmpName, bBossEmpName);
                    condition = checkValidate.checkCondition(isReferenceSBBoss);
                }

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
                }

                // condition이 "O" 또는 "T"일 경우, 적격 리스트에 추가
                if (condition == "O") {
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
            }

            workbook.close();
        } catch (Exception e) {

            e.printStackTrace();
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

    // 날짜 검색
    public List<MailResultDao> getData(Integer fromYear, Integer fromMonth,
                                       Integer toYear, Integer toMonth) {
        List<MailResultDao> validEmail = mapper.searchDateConditionX(fromYear, fromMonth, toYear, toMonth);
        return validEmail;
    }

}
