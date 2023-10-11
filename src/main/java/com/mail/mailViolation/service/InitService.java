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
		return mapper.findByNameAndUseYn(name)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));
	}

	// 팀장이 결재 후 실장 혹은 본부장이을 참초 or 결재 했는가?
	public boolean matchSBBoss(String exelEmpName, String sBossEmpName, String bBossEmpName) {
		return matchBoss(exelEmpName, sBossEmpName) || matchBoss(exelEmpName, bBossEmpName);
	}

	// 팀장이 결재 했는가?
	public boolean approvalTBoss(String lastApprover,
								Integer deptId) {
		// 팀장 이름 리스트
		List<String> tBossEmpNameList = findTBoss(deptId);

		// 하나라도 존재하는가?
		return tBossEmpNameList.stream()
				.anyMatch(empName -> empName.equals(lastApprover));
	}

	// 실장, 본부장 - 결재 혹은 참조로 match 확인
	public boolean matchBoss(String exelEmpName,
								String realEmpName) {

		if (exelEmpName.equals(realEmpName)) {
			return true;
		}
		return false;
	}

	// deptId로 팀장 찾기
	public List<String> findTBoss(Integer deptId) {
		return mapper.findTBoss(deptId)
				.orElseThrow(() -> new RuntimeException("팀장을 찾을 수 없음"));
	}

	// deptId로 실장 찾기
	public String findSBoss(Integer deptId) {
		return mapper.findSBoss(deptId)
				.orElseThrow(() -> new RuntimeException("실장을 찾을 수 없음"));
	}

	// deptId로 본부장 찾기
	public String findBBoss(Integer deptId){
		return mapper.findBBoss(deptId);
	}

	// 날짜 검색
	public List<MailResultDao> getData(Integer fromYear, Integer fromMonth,
									   Integer toYear, Integer toMonth) {
		List<MailResultDao> validEmail = mapper.searchDateConditionX(fromYear, fromMonth, toYear, toMonth);
		return validEmail;
	}
}