package com.mail.mailViolation.dto;

import lombok.*;

import java.time.LocalDate;


@Getter
@Builder
@AllArgsConstructor
public class EmployeeDao {
    private String empId;
    private String empName;
    private String deptId;
    private String empEmail;
    private String useYN;
    private LocalDate rgtDttm;
    private String apprReferYn;
}
