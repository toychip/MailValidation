package nice.mailViolation.service;

import nice.mailViolation.dto.MailResultDto;
import nice.mailViolation.mapper.MailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsertService {

    private final MailMapper mapper;

    // 데이터 삽입
    @Transactional
    public void insertData(List<MailResultDto> conditionOList,
                           List<MailResultDto> conditionXList,
                           List<MailResultDto> conditionTList) {

        if (isExists(conditionOList)) {
            for (MailResultDto mailResultDto : conditionOList) {
                mapper.insertValidResult(mailResultDto);
            }
        }

        if (isExists(conditionXList)) {
            for (MailResultDto mailResultDto : conditionXList) {
                mapper.insertValidResult(mailResultDto);
            }
        }

        if (isExists(conditionTList)) {
            for (MailResultDto mailResultDto : conditionTList) {
                mapper.insertValidResult(mailResultDto);
            }
        }
    }

    private static boolean isExists(List<MailResultDto> conditionList) {
        return conditionList != null && !conditionList.isEmpty();
    }
}

