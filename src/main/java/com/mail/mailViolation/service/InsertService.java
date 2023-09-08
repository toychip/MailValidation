package com.mail.mailViolation.service;

import com.mail.mailViolation.dto.MailResultDao;
import com.mail.mailViolation.mapper.MailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InsertService {

    private final MailMapper mapper;

    @Transactional
    public String insertData(List<MailResultDao> mailResultDaoList) {

        for (MailResultDao mailResultDao : mailResultDaoList) {
            mapper.insertValidResult(mailResultDao);
        }
        return null;
    }
}
