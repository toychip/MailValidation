package com.mail.mailViolation.comtroller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mail.mailViolation.dto.dto.ReturnDto;
import com.mail.mailViolation.service.InitService;
import com.mail.mailViolation.service.InsertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mail.mailViolation.dto.dto.FileUploadDto;
import com.mail.mailViolation.dto.dao.MailResultDao;
import com.mail.mailViolation.service.GetExelService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UploadController {

	private final GetExelService getExelService;
	private final InsertService insertService;
	private final InitService initService;

	@GetMapping("/upload")	// 업로드 페이지 VIEW 렌더링
	public String getMailForm(Model model, @ModelAttribute ArrayList<String> errors) {
		if (errors != null && !errors.isEmpty()) {
			model.addAttribute("errors", errors);
		}
		return "uploadForm";
	}

	@PostMapping("/upload")
	public String handleFileUpload(@Validated FileUploadDto form, BindingResult bindingResult,
								   RedirectAttributes redirectAttributes, Model model) {
		log.info("------------------------- 업로드 중");

		// 파일 유효성 검사 및 처리 로직
        MultipartFile file = null;
		if (form != null && form.getFile() != null) {
	            file = form.getFile();
	        }

		if (file == null || file.isEmpty()) {
			// 파일이 비어 있을 경우 오류 메시지 설정
			bindingResult.rejectValue("file", "error.file", "파일을 선택해주세요.");
		} else {
			String contentType = file.getContentType();
			if (contentType == null ||
					!(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
							contentType.equals("application/vnd.ms-excel") ||
							contentType.equals("application/vnd.ms-excel.sheet.macroEnabled.12") || // .xlsm
							contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) { // .xlsx
				bindingResult.rejectValue("file", "error.file", "엑셀 파일만 업로드 가능합니다.");
			}
		}

		if (!bindingResult.hasErrors()) {
			ReturnDto returnDto = getExelService.processExcelFile(file);
			// 검증 로직

			// 적격 리스트, 부적격 리스트 분리
			List<MailResultDao> conditionOList = returnDto.getConditionOList();
			List<MailResultDao> conditionXList = returnDto.getConditionXList();

			// 데이터 삽입
			insertService.insertData(conditionOList, conditionXList);

			// 검사 진행시 부적격 리스트 조회
			log.info("---------------------" + conditionXList.size());

			redirectAttributes.addFlashAttribute("conditionXList", conditionXList);
			log.info("안전하게 저장 성공");
			return "redirect:/validResult";
		} else {

			// 에러가 있을 경우 model에 담아서 return
			List<String> errors = bindingResult.getAllErrors().stream()
					.map(ObjectError::getDefaultMessage)
					.collect(Collectors.toList());
			model.addAttribute("errors", errors);

			return "uploadForm";
		}

//		적격 리스트 출력
//		for (MailResultDao mailResultDao : conditionOList) {
//			log.info("\n\n");
//			log.info("---------- conditionOList 문서 번호: " + mailResultDao.getDocNumber());
//			log.info("---------- conditionOList 메일 기안자: " + mailResultDao.getDraftsman());
//			log.info("---------- conditionOList 기안자 부서: " + mailResultDao.getDept());
//			log.info("---------- conditionOList 기안자 부서 코드: " + mailResultDao.getDeptId());
//			log.info("---------- conditionOList 적격 여부: " + mailResultDao.getResult());
//			log.info("---------- conditionOList 최종 결재자: " + mailResultDao.getLastApprover());
//			log.info("---------- conditionOList 결재일: " + mailResultDao.getApprovalDate());
//		}

//		부적격 리스트 출력
//		for (MailResultDao mailResultDao : conditionXList) {
//			log.info("\n\n");
//			log.info("---------- conditionXList 문서 번호: " + mailResultDao.getDocNumber());
//			log.info("---------- conditionXList 메일 기안자: " + mailResultDao.getDraftsman());
//			log.info("---------- conditionXList 기안자 부서: " + mailResultDao.getDept());
//			log.info("---------- conditionXList 기안자 부서 코드: " + mailResultDao.getDeptId());
//			log.info("---------- conditionXList 적격 여부: " + mailResultDao.getResult());
//			log.info("---------- conditionXList 최종 결재자: " + mailResultDao.getLastApprover());
//			log.info("---------- conditionXList 결재일: " + mailResultDao.getApprovalDate());
//		}
	}

//	DB연결 및 조회 테스트
//	@ResponseBody
	@GetMapping("/getList")
	public String getEmp(@RequestParam(required = false) Integer fromYear,
						 @RequestParam(required = false) Integer fromMonth,
						 @RequestParam(required = false) Integer toYear,
						 @RequestParam(required = false) Integer toMonth,
									  Model model, HttpServletRequest request
	) {

		// 검증 로직
		if (fromYear > toYear || (fromYear.equals(toYear) && fromMonth > toMonth)) {
			System.out.println("12312312312312312312                UploadController.getEmp");
			ArrayList<String> errors = new ArrayList<>();
			errors.add("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
			request.setAttribute("errors", errors);
			return "uploadForm"; // 현재 페이지로 리다이렉트
		}

		log.info("fromYear = " + fromYear);
		log.info("fromYear.getClass() = " + fromYear.getClass());
		log.info("fromMonth = " + fromMonth);
		log.info("toYear = " + toYear);
		log.info("toMonth = " + toMonth);
		List<MailResultDao> data = initService.getData(fromYear, fromMonth, toYear, toMonth);
		model.addAttribute("conditionXList", data);

		return "validResult";
	}
}
