package com.mail.mailViolation.dto.dto;

import com.mail.mailViolation.dto.dao.MailResultDao;
import lombok.Getter;

import java.util.List;

@Getter
public class ConditionListWrapper {
    private List<MailResultDao> conditionXList;
}
