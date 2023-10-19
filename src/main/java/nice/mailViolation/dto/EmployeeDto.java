package nice.mailViolation.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Builder
@AllArgsConstructor

// 보직좌를 확인하기 위해 DB에서 데이터를 꺼내오는 과정에서 필요한 객체
public class EmployeeDto {
    private Integer empId;
    private String empName;
    private BigDecimal deptId;
    private String empEmail;
    private String useYN;
    private LocalDate rgtDttm;
    private String apprReferYn;

    public static EmployeeDto getDefault() {
        return EmployeeDto.builder()
                .empId(-1)
                .empName("-1")
                .deptId(new BigDecimal("-1"))
                .empEmail("-1")
                .useYN("-1")
                .rgtDttm(LocalDate.now())
                .apprReferYn("-1")
                .build();
    }
}
