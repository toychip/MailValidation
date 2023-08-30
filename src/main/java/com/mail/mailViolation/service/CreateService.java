package com.mail.mailViolation.service;

import java.time.LocalDateTime;

import com.mail.mailViolation.dto.ApprovalMailDao;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreateService {

	public ApprovalMailDao createMailDao(Row row, String strYear) {
		
		log.info("------------------------------------------------------CreateService 호출");
		
		Integer docNumber = Integer.valueOf(row.getCell(0).toString());
		log.info("------------------------------------------------------draftsman 전호출");
		
		log.info("_________________________" + docNumber);
		String draftsman = row.getCell(1).toString();	// 기안자
		log.info("------------------------------------------------------dept 전호출");
		String dept = row.getCell(2).toString();		// 소속부서
		log.info("------------------------------------------------------title 전호출");
		String title = row.getCell(3).toString();	// 제목
		log.info("------------------------------------------------------temp 전호출");
		String temp = row.getCell(4).toString();
		
		log.info("------------------------------------------------------날짜 설정 전호출");
		
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
		
		log.info("------------------------------------------------------날짜 합치기 전호출");
		
		String mailTitle = row.getCell(5).toString();	// 메일 제목
		String recipient = row.getCell(6).toString();	// 받는 사람
		String reference = row.getCell(7).toString();	// 참조
		String blockCause = row.getCell(8).toString();	// 차단사유
		String lastApprover = row.getCell(9).toString();	// 최종 결재
		
		log.info("------------------------------------------------------생성 전호출");
		
		ApprovalMailDao buildApprovalMailDao = ApprovalMailDao.builder()
											.docNumber(docNumber)
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
		return buildApprovalMailDao;
	}

}
