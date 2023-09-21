package com.mail.mailViolation.dto.dao;

import lombok.*;

import java.time.LocalDate;


@Getter
@Builder
@AllArgsConstructor

// 보직좌를 확인하기 위해 DB에서 데이터를 꺼내오는 과정에서 필요한 객체
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
