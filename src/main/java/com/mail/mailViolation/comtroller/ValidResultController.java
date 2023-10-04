package com.mail.mailViolation.comtroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mail.mailViolation.dto.dao.MailResultDao;
import com.mail.mailViolation.service.SaveExelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
            List<MailResultDao> conditionXList = (List<MailResultDao>) model.getAttribute("conditionXList");
            String jsonConditionXList = objectMapper.writeValueAsString(conditionXList);

//            System.out.println(" 컨트롤러에서 jsonConditionXList = " + jsonConditionXList);

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
                              @RequestBody String conditionXList
    ){
        System.out.println("ValidResultController.downloadExcel");
        // 세션에 있는 부적격 리스트 추출

        System.out.println("=============== conditionXList = \n" + conditionXList);

        if (conditionXList != null && !conditionXList.isEmpty()) {
            try {
                // Excel 파일 생성 로직 호출
                System.out.println("try try try ValidResultController.downloadExcel");

                // saveExelService의 createExcelFile 메서드를 호출하여 Excel 파일 생성
                // 이 메서드는 ByteArrayInputStream 객체를 반환
                ByteArrayInputStream stream = saveExelService.createExcelFile(conditionXList);

                // HTTP Response 설정
                response.setContentType("application/vnd.ms-excel");

                // 다운로드될 파일의 이름을 설정
                response.setHeader("Content-Disposition", "attachment; filename=ValidResult.xlsx");

                // OutputStream을 사용하여 클라이언트에게 파일을 전송
                try (OutputStream os = response.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    // 스트림으로부터 데이터를 읽어 OutputStream에 쓴다
                    while ((bytesRead = stream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush(); // 버퍼를 비움
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
