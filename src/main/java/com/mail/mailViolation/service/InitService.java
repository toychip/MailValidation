package com.mail.mailViolation.service;

import java.time.LocalDateTime;
import java.util.List;

import com.mail.mailViolation.dto.request.ApprovalMailRequest;
import com.mail.mailViolation.dto.EmployeeDao;
import com.mail.mailViolation.mapper.MailMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitService {

	private final MailMapper mapper;

	public ApprovalMailRequest createMailDao(Row row, String strYear) {
		// ------------------------------------------------------
		log.info("------------------------------------------------------CreateService 호출");

		// 1.0 으로 된 사번 Integer 형으로 변환
		String strDocNumber = row.getCell(1).toString();
//		int intDocNumber = (int)Double.parseDouble(strDocNumber);
//		Integer docNumber = intDocNumber;

		log.info("------------------------------------------------------draftsman 전호출");

		String draftsman = row.getCell(2).toString();	// 기안자
		log.info("------------------------------------------------------dept 전호출");
		String dept = row.getCell(3).toString();		// 소속부서
		log.info("------------------------------------------------------title 전호출");
		String title = row.getCell(4).toString();	// 제목
		log.info("------------------------------------------------------temp 전호출");
		String temp = row.getCell(5).toString();

		log.info("------------------------------------------------------날짜 추출 =" + temp);

		log.info("------------------------------------------------------날짜 설정 전호출");

		log.info("------------------------------------------------------year =" + strYear);
		// 년
		int year = Integer.valueOf(strYear);


		// 월
		int month = Integer.valueOf(temp.substring(0, 2));
		log.info("------------------------------------------------------month =" + month);

		// 일
		int day = Integer.valueOf(temp.substring(3, 5));
		log.info("------------------------------------------------------day =" + day);

		// 시
		int hour = Integer.valueOf(temp.substring(6, 8));
		log.info("------------------------------------------------------hour =" + hour);

		// 분
		int minutes = Integer.valueOf(temp.substring(9, 11));
		log.info("------------------------------------------------------minutes =" + minutes);
		
		LocalDateTime ApprovalDate = LocalDateTime.of(year, month, day, hour, minutes);
		
		log.info("------------------------------------------------------날짜 합치기 전호출");
		
		String mailTitle = row.getCell(6).toString();	// 메일 제목
		String recipient = row.getCell(7).toString();	// 받는 사람
		String reference = row.getCell(8).toString();	// 참조
		String blockCause = row.getCell(9).toString();	// 차단사유
		String lastApprover = row.getCell(10).toString();	// 최종 결재
		
		log.info("------------------------------------------------------생성 전호출");
		
		ApprovalMailRequest buildApprovalMailRequest = ApprovalMailRequest.builder()
											.docNumber(strDocNumber)
											.draftsman(draftsman)
											.dept(dept)
											.title(title)
											.ApprovalDate(ApprovalDate)
											.mailTitle(mailTitle)
											.recipient(recipient)
											.reference(reference)
											.blockCause(blockCause)
											.lastApprover(lastApprover)
											.build();
		log.info("------------------------------------------------------return 전호출");
		return buildApprovalMailRequest;
	}

	// Y, N 등 여러개의 리스트 반환시 가장 최신 것으로 반환하는 메서드
	public EmployeeDao getEmp(String name) {
		List<EmployeeDao> result = mapper.findEmployeeByDraftsmanAndApprReferYn(name);
		EmployeeDao latestEmployee = null;

		for (EmployeeDao employeeDao : result) {
			char userYN = employeeDao.getUseYN().charAt(0);
			if(userYN == 'Y'){
				return employeeDao;
			}
		}

		for (EmployeeDao employeeDao : result) {
			if (latestEmployee == null) {
				latestEmployee = employeeDao;
				continue;
			}
			if (employeeDao.getRgtDttm().isAfter(latestEmployee.getRgtDttm())) {
				latestEmployee = employeeDao;
			}
		}

		return latestEmployee;
	}

	public boolean checkApprovalCondition(ApprovalMailRequest approvalMailRequest,
										  Long validOverLapDeptId) {

		List<EmployeeDao> bosses = mapper.findBossByDeptId(validOverLapDeptId);

		for (EmployeeDao boss : bosses) {
			System.out.println("InitService.checkApprovalCondition.forloop");
			System.out.println("------------------------boss = " + boss);
			if (boss.getEmpName().equals(approvalMailRequest.getLastApprover()) ||
					approvalMailRequest.getReference().contains(boss.getEmpName()) ||
					approvalMailRequest.getReference().contains(boss.getEmpEmail())) {
				return true;
			}
		}
		return false;
	}
}
