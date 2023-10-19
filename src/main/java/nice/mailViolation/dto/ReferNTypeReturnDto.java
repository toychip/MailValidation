package nice.mailViolation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class ReferNTypeReturnDto {

    private String condition;

    private ReasonIneligibility reasonIneligibility;
}
