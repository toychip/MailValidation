package com.mail.mailViolation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BossInfo {

    private BigDecimal deptId;
    private String name;
    private String email;

}
