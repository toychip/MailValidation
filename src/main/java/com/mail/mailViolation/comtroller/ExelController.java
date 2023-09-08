package com.mail.mailViolation.comtroller;

import java.util.List;

import com.mail.mailViolation.dto.response.ReturnDto;
import com.mail.mailViolation.service.InitService;
import com.mail.mailViolation.service.InsertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mail.mailViolation.dto.request.FileUploadRequest;
import com.mail.mailViolation.dto.MailResultDao;
import com.mail.mailViolation.service.GetExelService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ExelController {

	private final GetExelService getExelService;
	private final InitService initService;
	private final InsertService insertService;

	@GetMapping("/upload")
	public String getMailForm(Model model) {
		//		@ModelAttribute FileUploadRequest form, Model model
		//		model.addAllAttribute("exelForm")
//		model.getAttribute()
		return "uploadForm";
	}

	@PostMapping("/upload")
	public String handleFileUpload(FileUploadRequest form, HttpSession session) {

		log.info("------------------------- 업로드 중");

		// 파일 유효성 검사 및 처리 로직
        MultipartFile file = null;
		if (form != null && form.getFile() != null) {
	            file = form.getFile();
	        }

		// 파일이 비어있을 경우 오류메시지 설정 로직 필요
//		log.info("-------------------------서비스 처리 전 로그");


		ReturnDto returnDto = getExelService.processExcelFile(file);
		List<MailResultDao> conditionOList = returnDto.getConditionOList();
		List<MailResultDao> conditionXList = returnDto.getConditionXList();
//		List<MailResultDao> mailResultDaoList = exelService.processExcelFile(file);

		for (MailResultDao mailResultDao : conditionOList) {
			log.info("\n\n");
			log.info("---------- conditionOList 문서 번호: " + mailResultDao.getDocNumber());
			log.info("---------- conditionOList 메일 기안자: " + mailResultDao.getDraftsman());
			log.info("---------- conditionOList 기안자 부서: " + mailResultDao.getDept());
			log.info("---------- conditionOList 기안자 부서 코드: " + mailResultDao.getDeptId());
			log.info("---------- conditionOList 적격 여부: " + mailResultDao.getResult());
			log.info("---------- conditionOList 최종 결재자: " + mailResultDao.getLastApprover());
			log.info("---------- conditionOList 결재일: " + mailResultDao.getApprovalDate());
		}

		for (MailResultDao mailResultDao : conditionXList) {
			log.info("\n\n");
			log.info("---------- conditionXList 문서 번호: " + mailResultDao.getDocNumber());
			log.info("---------- conditionXList 메일 기안자: " + mailResultDao.getDraftsman());
			log.info("---------- conditionXList 기안자 부서: " + mailResultDao.getDept());
			log.info("---------- conditionXList 기안자 부서 코드: " + mailResultDao.getDeptId());
			log.info("---------- conditionXList 적격 여부: " + mailResultDao.getResult());
			log.info("---------- conditionXList 최종 결재자: " + mailResultDao.getLastApprover());
			log.info("---------- conditionXList 결재일: " + mailResultDao.getApprovalDate());
		}

		insertService.insertData(conditionOList);
		insertService.insertData(conditionXList);

		session.setAttribute("conditionXList", conditionXList);
		System.out.println("안전하게 저장 성공");
		return "redirect:/validResult";
	}

	@ResponseBody
	@GetMapping("/getList")
	public List<MailResultDao> getEmp() {
		List<MailResultDao> data = initService.getData();
		return data;
	}
}
