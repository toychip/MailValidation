package com.mail.mailViolation.comtroller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Slf4j
@ControllerAdvice(assignableTypes = {
        UploadController.class, ValidResultController.class
})
public class AdviceController {

    @ResponseStatus(HttpStatus.OK) // 예외 발생 시 200
    @ExceptionHandler(Exception.class)  // 전체 예외로 잡음
    public String resolveEx(Exception e,Model model) {
        log.info(" ------------------------------------------------------------- ");
        e.printStackTrace();
        log.info(" ------------------------------------------------------------- ");

        log.info("AdviceController.resolveEx");
        ArrayList<String> errors = new ArrayList<>();
        errors.add("올바른 exel이 아닙니다. 템플릿에 맞게 등록해주세요.");
        model.addAttribute("errors", errors);  // 모델에 에러 메시지 리스트 추가
        return "uploadForm";
    }
}
