package com.mail.mailViolation.dto;

import lombok.*;

import java.time.LocalDate;


@Getter
@Builder
@AllArgsConstructor
public class EmployeeDao {
    private Integer empId;
    private String empName;
    private Integer deptId;
    private String empEmail;
    private String useYN;
    private LocalDate rgtDttm;
    private String apprReferYn;

    public static EmployeeDao getDefault() {
        return EmployeeDao.builder()
                .empId(-1)
                .empName("-1")
                .deptId(-1)
                .empEmail("-1")
                .useYN("-1")
                .rgtDttm(LocalDate.now())
                .apprReferYn("-1")
                .build();
    }
}
