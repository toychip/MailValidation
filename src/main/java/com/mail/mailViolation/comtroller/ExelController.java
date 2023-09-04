package com.mail.mailViolation.comtroller;

import java.util.List;

import com.mail.mailViolation.dto.EmployeeDao;
import com.mail.mailViolation.mapper.MailMapper;
import com.mail.mailViolation.service.CreateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mail.mailViolation.dto.request.FileUploadRequest;
import com.mail.mailViolation.dto.response.ValidationResponse;
import com.mail.mailViolation.service.ExelService;

import lombok.RequiredArgsConstructor;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ExelController {

	private final ExelService exelService;
	private final CreateService createService;

	@GetMapping("/upload")
	public String getMailForm() {
//		@ModelAttribute FileUploadRequest form, Model model
//		model.addAllAttribute("exelForm")
		return "uploadForm";
	}
	
	@PostMapping("/upload")
	public String handleFileUpload(FileUploadRequest form) {
		
		log.info("-------------------------파일 처리 전 로그");

		// 파일 유효성 검사 및 처리 로직
        MultipartFile file = null;
		if (form != null && form.getFile() != null) {
	            file = form.getFile();
	        }
		
		// 파일이 비어있을 경우 오류메시지 설정 로직 필요
		log.info("-------------------------서비스 처리 전 로그");
		List<ValidationResponse> validationResponseList = null;
		validationResponseList = exelService.processExcelFile(file);
		return "redirect:/upload";
	}

	@ResponseBody
	@GetMapping("/getEmp")
	public List<EmployeeDao> getEmp() {
		List<EmployeeDao> result = createService.getEmp("한상익");
		return result;
	}
}
