package com.mail.mailViolation.dto;

import lombok.*;


@Getter
@Builder
@AllArgsConstructor
public class EmployeeDao {
    private String empId;
    private String empName;
    private String deptId;
    private String empEmail;
    private String apprReferYn;
}
