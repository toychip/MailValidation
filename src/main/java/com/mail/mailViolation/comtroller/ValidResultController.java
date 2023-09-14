package com.mail.mailViolation.comtroller;

import com.mail.mailViolation.dto.MailResultDao;
import com.mail.mailViolation.service.SaveExelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ValidResultController {

    private final SaveExelService saveExelService;

    @GetMapping("/validResult")
    public String showValidResult(Model model, HttpSession session) {
        List<MailResultDao> conditionXList = (List<MailResultDao>) session.getAttribute("conditionXList");
        if (conditionXList != null && !conditionXList.isEmpty()) {
            model.addAttribute("conditionXList", conditionXList);
            System.out.println("---------------- Session attribute exists !!");
        } else {
            System.out.println("Session attributes is not exists");
        }
        return "validResult";
    }

    @PostMapping("/downloadExcel")
    public void downloadExcel(HttpServletResponse response, HttpSession session) {
        List<MailResultDao> conditionXList = (List<MailResultDao>) session.getAttribute("conditionXList");
        if (conditionXList != null && !conditionXList.isEmpty()) {
            try {
                // Excel 파일 생성 로직 호출
                ByteArrayInputStream stream = saveExelService.createExcelFile(conditionXList);

                // HTTP Response 설정
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=mailResult.xlsx");

                try (OutputStream os = response.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = stream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        return "redirect:/validResult";
    }
}
