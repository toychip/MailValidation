package com.mail.mailViolation.service;

import java.time.LocalDateTime;
import java.util.*;

import com.mail.mailViolation.dto.dao.MailResultDao;
import com.mail.mailViolation.dto.dto.ApprovalMailDto;
import com.mail.mailViolation.dto.dao.EmployeeDao;
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

	public ApprovalMailDto createMailDao(Row row, String strYear) {


		String strDocNumber = row.getCell(1).toString();
		String draftsman = row.getCell(2).toString();    // 기안자
		String dept = row.getCell(3).toString();        // 소속부서
		String title = row.getCell(4).toString();    // 제목
		String temp = row.getCell(5).toString();

		// 년
		int year = Integer.valueOf(strYear);
		// 월
		int month = Integer.valueOf(temp.substring(0, 2));
		// 일
		int day = Integer.valueOf(temp.substring(3, 5));
		// 시
		int hour = Integer.valueOf(temp.substring(6, 8));
		// 분
		int minutes = Integer.valueOf(temp.substring(9, 11));

		LocalDateTime ApprovalDate = LocalDateTime.of(year, month, day, hour, minutes);
		String mailTitle = row.getCell(6).toString();    // 메일 제목
		String recipient = row.getCell(7).toString();    // 받는 사람
		String reference = row.getCell(8).toString();    // 참조
		String blockCause = row.getCell(9).toString();    // 차단사유
		String lastApprover = row.getCell(10).toString();    // 최종 결재

		ApprovalMailDto buildApprovalMailDto = ApprovalMailDto.builder()
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
		return buildApprovalMailDto;
	}

	// Y, N 등 여러개의 리스트 반환시 가장 최신 것으로 반환하는 메서드
	public EmployeeDao getEmp(String name) {
		List<EmployeeDao> result = mapper.findEmployeeByDraftsmanAndApprReferYn(name);
		EmployeeDao latestEmployee = null;

		if (result.isEmpty()) {
			return EmployeeDao.getDefault();
		}

		for (EmployeeDao employeeDao : result) {

			char userYN = employeeDao.getUseYN().charAt(0);
			if (userYN == 'Y') {
				return employeeDao;
			}
		}

		// DB에 없는 사람이 기안자일 수 없으므로 DB에 없는 경우 생각 안함
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

	List<Integer> arrays = new ArrayList<>();

	public String checkApprovalCondition(ApprovalMailDto approvalMailDto,
										 Integer validOverLapDeptId) {

		List<EmployeeDao> bosses = mapper.findBossByDeptId(validOverLapDeptId);

//		log.info("----------InitService.checkApprovalCondition 시작 \n\n");
//		log.info("----------최종 결재자: " + approvalMailRequest.getLastApprover());
//
//		log.info("----------참조: " + approvalMailRequest.getReference());
//		log.info("----------부서: " + approvalMailRequest.getDept());

//		기안자의 부서에서 상위 보직좌가 존재하는지 확인하는 로직
//		if (bosses.size() == 0) {
//			if (arrays.stream().noneMatch(id -> id.equals(validOverLapDeptId))) {
//				arrays.add(validOverLapDeptId);
//			}
//			log.info("----------------- boss 사이즈가 0입니다. 심각한 사항입니다.");
//			log.info("----------------- 문서번호: " + approvalMailRequest.getDocNumber());
//			log.info("----------------- 기안자 부서번호: " + validOverLapDeptId);
//			log.info("----------------- 부서: " + approvalMailRequest.getDept());
//			log.info("----------------- 기안자: " + approvalMailRequest.getDraftsman());
//		}

		// 중복된 보스 이름을 가진 객체 중 가장 최신의 RGT_DTTM 값을 가진 객체만을 유지
		Map<String, EmployeeDao> uniqueBosses = new HashMap<>();
		for (EmployeeDao boss : bosses) {
			EmployeeDao existingBoss = uniqueBosses.get(boss.getEmpName());
			if (existingBoss == null || boss.getRgtDttm().isAfter(existingBoss.getRgtDttm())) {
				uniqueBosses.put(boss.getEmpName(), boss);
			}
		}

		for (EmployeeDao boss : uniqueBosses.values()) {
//			log.info("----------------- 최종 결재자 혹은 포함되는지 확인 중 -----------------");
//			log.info("부서 대빵 보스의 아이디 boss.getDeptId() = " + boss.getDeptId());
//			log.info("부서 대빵 보스의 이름 boss.getEmpName() = " + boss.getEmpName());
//			log.info("부서 대빵 보스의 이메일 boss.getEmpEmail() = " + boss.getEmpEmail());
//			log.info("부서 대빵 보스의 부서코드.getDeptId() = " + boss.getDeptId());

			if (boss.getEmpName().equals(approvalMailDto.getLastApprover()) ||
					approvalMailDto.getReference().contains(boss.getEmpName()) ||
					approvalMailDto.getReference().contains(boss.getEmpEmail())) {
//				log.info("----------------- 적격 조건 탐 InitService.checkApprovalCondition -----------------");
//				log.info("----------------- 확인 끝 -----------------");
				return "O";
			}
		}
//		log.info("----------------- 부적격 -----------------");
//		log.info("----------------- approvalMailRequest = " + approvalMailRequest.getDept());
//		log.info("----------------- 확인 끝 -----------------");
		return "X";

		// 메일 테스트는 emp 없으니까 예외처리 부적격 적격에는 넣지 않고 y or a 일때
		// 메일에는 있는데 emp에는 없는 애들은 따로 예외처리 적격 부적격 판단 로직 x
		// A, T 등
	}

	// 보스가 없는 사람들만 추출 - 테스트 용
	public List<Integer> getNoBossDepartments() {
		return new ArrayList<>(arrays); // 멤버 변수를 직접 반환하지 않고 복사본을 반환 }
	}

	// 전체 결과 조회 - 테스트 용
	public List<MailResultDao> getData(Integer fromYear, Integer fromMonth,
									   Integer toYear, Integer toMonth) {
		List<MailResultDao> validEmail = mapper.searchDateConditionX(fromYear, fromMonth, toYear, toMonth);
		return validEmail;
	}

}