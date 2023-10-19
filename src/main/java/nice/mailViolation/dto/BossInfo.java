package nice.mailViolation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class BossInfo {

    private BigDecimal deptId;
    private String name;
    private String email;


    public static BossInfo defaultValue() {
        return BossInfo.builder()
                .deptId(new BigDecimal(-1))
                .name("존재하지 않음 - 기본값")
                .email("존재하지 않음 - 기본값")
                .build();
    }

}
