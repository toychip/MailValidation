package nice.mailViolation.comtroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nice.mailViolation.dto.MailResultDto;
import nice.mailViolation.service.SaveExelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ValidResultController {

    private final ObjectMapper objectMapper;

    private final SaveExelService saveExelService;

    // 검사 후 검증 결과 페이지 렌더링
    @GetMapping("/validResult")
    public String showValidResult(Model model) {
        try {
            List<MailResultDto> conditionXList = (List<MailResultDto>) model.getAttribute("conditionXList");
            String jsonConditionXList = objectMapper.writeValueAsString(conditionXList);

//            log.info(" 컨트롤러에서 jsonConditionXList = " + jsonConditionXList);

            if (conditionXList != null && !conditionXList.isEmpty()) {
                model.addAttribute("conditionXList", jsonConditionXList);
                log.info("---------------- model attribute exists !!");
            } else {
                log.info("---------------- model attributes is not exists");
            }
            return "validResult";
        } catch (JsonProcessingException e) {
            log.error("JSON processing failed", e);
            return "errorPage";  // 적절한 에러 페이지로 리다이렉트
        }
    }

    @PostMapping("/downloadExcel")
    public void downloadExcel(HttpServletResponse response,
                              @RequestBody List<MailResultDto> conditionXList) throws IOException {

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=mail_results.xlsx");
        saveExelService.exportToExcel(response, conditionXList);
    }
    
}
