package com.mail.mailViolation.service;

import com.mail.mailViolation.dto.MailResultDto;
import com.mail.mailViolation.mapper.MailMapper;
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
                           List<MailResultDto> conditionXList) {

        for (MailResultDto mailResultDto : conditionOList) {
            mapper.insertValidResult(mailResultDto);
        }

        for (MailResultDto mailResultDto : conditionXList) {
            mapper.insertValidResult(mailResultDto);
        }
    }
}
