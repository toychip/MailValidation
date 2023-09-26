package com.mail.mailViolation.exception;

import com.mail.mailViolation.comtroller.UploadController;
import com.mail.mailViolation.comtroller.ValidResultController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;

@Slf4j
@ControllerAdvice(assignableTypes = {
        UploadController.class, ValidResultController.class
})
public class AdviceController {

    @ResponseStatus(HttpStatus.OK) // 예외 발생 시 200
    @ExceptionHandler(ExelUploadException.class)  // 전체 예외로 잡음
    public String resolveEx(ExelUploadException e,Model model) {
        log.info(" ------------------------에러 사유----------------------------- ");
        e.printStackTrace();
        log.info(" ------------------------------------------------------------- ");

        log.info("AdviceController.resolveEx");
        ArrayList<String> errors = new ArrayList<>();
        errors.add("올바른 exel이 아닙니다. 템플릿에 맞게 등록해주세요.");
        model.addAttribute("errors", errors);  // 모델에 에러 메시지 리스트 추가
        return "uploadForm";
    }
}
