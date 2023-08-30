package com.mail.mailViolation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter // model에 담아야하기 때문에 setter 불가피
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResponse {
	private String docNumber;	// 문서 번호
	private String draftsman;	// 기안자
	private String dept;		// 소속부서
	private String title;		// 제목
	private LocalDateTime ApprovalDate;	// 결재일
	private String mailTitle;	// 메일 제목
	private String recipient;	// 받는 사람
	private String reference;	// 참조
	private String blockCause;	// 차단사유
	private String lastApprover;	// 최종 결재
	private String result; 		// 적격 여부 일단 1 or 0 으로 설정. 추후 보안사항 위반일 경우 위반의 해당하는 값 1, 2, 3, .. 기입
	
}
