package com.mail.mailViolation.comtroller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mail.mailViolation.dto.ReturnDto;
import com.mail.mailViolation.service.InsertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mail.mailViolation.dto.FileUploadDto;
import com.mail.mailViolation.dto.MailResultDto;
import com.mail.mailViolation.service.GetExelService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UploadController {

	private final GetExelService getExelService;
	private final InsertService insertService;

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
			List<MailResultDto> conditionOList = returnDto.getConditionOList();
			List<MailResultDto> conditionXList = returnDto.getConditionXList();

			// 데이터 삽입
			insertService.insertData(conditionOList, conditionXList);

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
	}

//	DB연결 및 조회 테스트
//	@ResponseBody
	@GetMapping("/getList")
	public String getEmp(@RequestParam(required = false, defaultValue = "9999") Integer fromYear,
						 @RequestParam(required = false, defaultValue = "99") Integer fromMonth,
						 @RequestParam(required = false, defaultValue = "9999") Integer toYear,
						 @RequestParam(required = false, defaultValue = "99") Integer toMonth,
									  RedirectAttributes redirectAttributes, HttpServletRequest request
	) {

		if (fromYear.equals(9999) && fromMonth.equals(99)) {
			fromYear = 1900;
			fromMonth = 01;
		}

		if (toYear.equals(9999) && toMonth.equals(99)) {
			LocalDate now = LocalDate.now();
			toYear = now.getYear();
			toMonth = now.getMonthValue();
		}

		// 검증 로직
		if (fromYear > toYear || (fromYear.equals(toYear) && fromMonth > toMonth)) {
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

		List<MailResultDto> data = getExelService.getData(fromYear, fromMonth, toYear, toMonth);

		redirectAttributes.addFlashAttribute("fromYear", fromYear);
		redirectAttributes.addFlashAttribute("fromMonth", fromMonth);
		redirectAttributes.addFlashAttribute("toYear", toYear);
		redirectAttributes.addFlashAttribute("toMonth", toMonth);

		redirectAttributes.addFlashAttribute("conditionXList", data);

		return "redirect:/validResult";
	}
}
