package com.mail.mailViolation.dto;

import lombok.*;

import java.time.LocalDate;


@Getter
@Builder
@AllArgsConstructor
public class EmployeeDao {
    private Long empId;
    private String empName;
    private Long deptId;
    private String empEmail;
    private String useYN;
    private LocalDate rgtDttm;
    private String apprReferYn;

    public static EmployeeDao getDefault() {
        return EmployeeDao.builder()
                .empId(-1L)
                .empName("-1")
                .deptId(-1L)
                .empEmail("-1")
                .useYN("-1")
                .rgtDttm(LocalDate.now())
                .apprReferYn("-1")
                .build();
    }
}
