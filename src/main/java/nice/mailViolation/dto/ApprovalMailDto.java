package nice.mailViolation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;



@Getter
@Builder
@AllArgsConstructor

// 엑셀에서 값을 꺼내서 바인딩할 객체
public class ApprovalMailDto {
	
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
}
