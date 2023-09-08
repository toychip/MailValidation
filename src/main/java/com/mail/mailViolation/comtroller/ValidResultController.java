package com.mail.mailViolation.comtroller;

import com.mail.mailViolation.dto.MailResultDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ValidResultController {

    // 새로운 /validResult GET 컨트롤러
    @GetMapping("/validResult")
    public String showValidResult(Model model) {
        // Flash attribute에서 데이터 가져오기
        if (model.containsAttribute("conditionXList")) {
            List<MailResultDao> conditionXList =
                    (List<MailResultDao>) model.getAttribute("conditionXList");
            // 이제 conditionXList를 뷰에서 사용할 수 있습니다.
        }
        return "validResult";  // 결과를 보여줄 HTML 페이지 이름
    }

}
