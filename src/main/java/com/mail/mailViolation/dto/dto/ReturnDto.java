package com.mail.mailViolation.dto.dto;

import com.mail.mailViolation.dto.dao.MailResultDao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor

// 검증 결과 후 적격 리스트와 부적격 리스트
public class ReturnDto {

    private List<MailResultDao> conditionXList;
    private List<MailResultDao> conditionOList;
}
