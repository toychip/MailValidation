package com.mail.mailViolation.service;

import com.mail.mailViolation.dto.dao.MailResultDao;
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
    public void insertData(List<MailResultDao> conditionOList,
                           List<MailResultDao> conditionXList) {

        for (MailResultDao mailResultDao : conditionOList) {
            mapper.insertValidResult(mailResultDao);
        }

        for (MailResultDao mailResultDao : conditionXList) {
            mapper.insertValidResult(mailResultDao);
        }
    }
}
