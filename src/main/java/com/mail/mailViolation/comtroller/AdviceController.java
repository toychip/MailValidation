package com.mail.mailViolation.comtroller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.exceptions.TemplateAssertionException;

@ControllerAdvice
public class AdviceController {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public String resolveEx(Model model) {

        model.addAttribute("errors", "올바른 exel이 아닙니다. 템플릿에 맞게 등록해주세요.");
        return "error-page";
    }
}
