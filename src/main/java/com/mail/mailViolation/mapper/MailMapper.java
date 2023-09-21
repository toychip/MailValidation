package com.mail.mailViolation.mapper;

import com.mail.mailViolation.dto.dao.EmployeeDao;
import com.mail.mailViolation.dto.dao.MailResultDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MailMapper {

    List<EmployeeDao> findEmployeeByDraftsmanAndApprReferYn(@Param("draftsman") String draftsman);

    List<EmployeeDao> findBossByDeptId(@Param("deptId") Integer validOverLapDeptId);

    void insertValidResult(MailResultDao mailResultDao);

    List<MailResultDao> findValidEmail();
}
