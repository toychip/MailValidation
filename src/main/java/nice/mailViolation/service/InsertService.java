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
        if (conditionOList != null && !conditionOList.isEmpty()){
            for (MailResultDto mailResultDto : conditionOList) {
                mapper.insertValidResult(mailResultDto);
            }
        }


        if (conditionXList != null && !conditionXList.isEmpty()){
            for (MailResultDto mailResultDto : conditionXList) {
                mapper.insertValidResult(mailResultDto);
            }
        }

        if (conditionTList != null && !conditionTList.isEmpty()){
            for (MailResultDto mailResultDto : conditionTList) {
                mapper.insertValidResult(mailResultDto);
            }
        }
    }
}

