package com.mail.mailViolation.dto.dto;

import com.mail.mailViolation.dto.dao.MailResultDao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ConditionListWrapper {
    private List<MailResultDao> conditionXList;
}
