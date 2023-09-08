package com.mail.mailViolation.dto.response;

import com.mail.mailViolation.dto.MailResultDao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReturnDto {

    private List<MailResultDao> conditionXList;
    private List<MailResultDao> conditionOList;
}
