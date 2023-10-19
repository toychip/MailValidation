package nice.mailViolation.service;

import nice.mailViolation.dto.*;
import nice.mailViolation.exception.ExelUploadException;
import nice.mailViolation.mapper.MailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
        List<MailResultDto> conditionXList = new ArrayList<>();
        List<MailResultDto> conditionOList = new ArrayList<>();

        checkValidate.loadBossInfoToMemory();

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
//            for (int i = 3; i <= 200; i++) {
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
                EmployeeDto findEmp = checkValidate.getEmp(approvalMailDto.getDraftsman());
                BigDecimal empDeptId = findEmp.getDeptId();
//                log.info("-------------------- empDeptId = " + empDeptId);

                // 부적격 사유
                ReasonIneligibility reasonIneligibility;

                ConditionAndReasonIneligibility conditionAndReasonIneligibility;

                String condition = "X";
                String title = approvalMailDto.getTitle();
//                log.info("-------------------- title = " + title);

                // 기안자 등급
                String apprReferYn = findEmp.getApprReferYn();
//                log.info("-------------------- 등급 = " + apprReferYn);

                // 결재자
                String lastApprover = approvalMailDto.getLastApprover();
//                log.info("-------------------- 결재 = " + lastApprover);

                // 참조
                String referencer = approvalMailDto.getReference();
//                log.info("-------------------- 참조 = " + referencer);

                // 실장
                BossInfo sBoss = checkValidate.findSBoss(empDeptId);
                String sBossEmpName = sBoss.getName();
                String sBossEmail = sBoss.getEmail();
//                log.info("-------------------- 실장 이름 = " + sBossEmpName);

                // 본부장
                BossInfo bBoss = checkValidate.findBBoss(empDeptId);
                String bBossEmpName = bBoss.getName();
                String bBossEmail = bBoss.getEmail();
//                log.info("-------------------- 본부장 이름 = " + bBossEmpName);

                if (approvalMailDto.getDept().contains("그룹웨어관리")) {
                    condition = "T";
                }

                // 결재를 받으려는 사람이 일반 사원일 경우
                if ("N".equals(apprReferYn)) {
//                    log.info("나는 일반 사원입니다 내 이름은 " + findEmp.getEmpName());

                    // 팀장이 결재했는가?
                    boolean isTBossApprover = checkValidate.approvalTBoss(empDeptId, lastApprover);


                    // 일반사원일 때의 부적격 검증
                    conditionAndReasonIneligibility = checkValidate.basicEmployee(
                            isTBossApprover, lastApprover, referencer,
                            sBossEmpName, sBossEmail,
                            bBossEmpName, bBossEmail);

                    condition = conditionAndReasonIneligibility.getCondition();
                    reasonIneligibility = conditionAndReasonIneligibility.getReasonIneligibility();

                }

                // 결재를 받으려는 사람이 팀장일 경우
                if ("T".equals(apprReferYn)) {
//                    log.info("나는 팀장입니다 내 이름은 " + findEmp.getEmpName());

                    // 실장 결재
                    boolean isSBossApprover = checkValidate.isSBossApprover(lastApprover, sBossEmpName);
                    // 본부장 결재
                    boolean isBBossApprover = checkValidate.isBBossApprover(lastApprover, bBossEmpName);

                    // 실장 혹은 본부장에게 결재를 받았는가?
                    boolean currentState = isSBossApprover || isBBossApprover;

                    condition = checkValidate.checkCondition(currentState);

                    // 팀장이고, 실장 혹은 본부장 중 아무에게도 결재를 받지 않음
                    if (condition.equals("X")) {
                        reasonIneligibility = ReasonIneligibility.E;
                    }

                }

                // 결재를 받으려는 사람이 실장일 경우
                if ("S".equals(apprReferYn)) {
                    // 본부장이 결재한 경우
//                    log.info("나는 실장입니다 내 이름은 " + findEmp.getEmpName());

                    boolean isBBossApprover = checkValidate.isBBossApprover(lastApprover, bBossEmpName);

                    condition = checkValidate.checkCondition(isBBossApprover);

//                    실장이고, 본부장에게 결재를 받지 않음
                    if (condition.equals("X")) {
                        reasonIneligibility = ReasonIneligibility.F;
                    }
                }

                // DB 관리자인 it 혁신실 팀장, 결재 관리하는 경영지원실 팀장 및 실장일 경우
                boolean masterApproval = (lastApprover.equals(managementTeamTBoss))
                        || (lastApprover.equals(managementTeamSBoss))
                        || (lastApprover.equals(itInnovationTeamTBoss));

                // 일반사원 or 팀장이고, 부적격 상태이고, (최종 DB 관리자 및 경영지원실 팀장 or 실장이 결재한 경우
                if (condition.equals("X") && masterApproval && !apprReferYn.equals("S") ) {

                    // 본인 부서의 실장 혹은 본부장을 참조했는가?
                    boolean isSBossReferer = checkValidate.isSBossReferer(referencer, sBossEmpName, sBossEmail);
                    boolean isBBossReferer = checkValidate.isBBossReferer(referencer, bBossEmpName, bBossEmail);

                    boolean isSBReferer = isSBossReferer || isBBossReferer;

                    condition = checkValidate.checkCondition(isSBReferer);

                    // 일반 사원이고, 경영지원실 팀장, 실장, DB관리자 중 한 명에게 결재를 받았지만, 본인 부서의 보직좌를 참조하지 않음.
                    if (apprReferYn.equals("N") && condition.equals("X")) {
                        reasonIneligibility = ReasonIneligibility.G;
                    }

                    // 팀장 이고, 경영지원실 팀장, 실장, DB관리자 중 한 명에게 결재를 받았지만, 본인 부서의 보직좌를 참조하지 않음.
                    if (apprReferYn.equals("T") && condition.equals("X")) {
                        reasonIneligibility = ReasonIneligibility.H;
                    }
                }

                // 실장이고, 부적격 상태이고, (최종 DB 관리자 및 경영지원실 팀장 or 실장이 결재한 경우
                if (condition.equals("X") && masterApproval && apprReferYn.equals("S") ) {

                    // 본인 부서의 본부장을 참조했는가?
                    boolean isBBossReferer = checkValidate.isBBossReferer(referencer, bBossEmpName, bBossEmail);

                    condition = checkValidate.checkCondition(isBBossReferer);

                    System.out.println("GetExelService.processExcelFile");
                    // DT 실은 본부가 없음
                    if(findEmp.getEmpName().equals("유성훈")) {
                        log.info("findemp.getempName() = " + findEmp.getEmpName());
                        condition = "O";
                        reasonIneligibility = ReasonIneligibility.A;
                    }
                }

                // condition이 "X"일 경우, 부적격 리스트에 추가
                if ("X".equals(condition)) {
                    conditionXList.add(
                            MailResultDto.builder()
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
                if ("O".equals(condition)) {
                    conditionOList.add(
                            MailResultDto.builder()
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
    public List<MailResultDto> getData(Integer fromYear, Integer fromMonth,
                                       Integer toYear, Integer toMonth) {
        return mapper.searchDateConditionX(fromYear, fromMonth, toYear, toMonth);
    }

}
