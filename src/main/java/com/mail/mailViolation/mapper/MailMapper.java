package com.mail.mailViolation.mapper;

import com.mail.mailViolation.dto.EmployeeDao;
import com.mail.mailViolation.dto.MailResultDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MailMapper {

    List<EmployeeDao> findEmployeeByDraftsmanAndApprReferYn(@Param("draftsman") String draftsman);

    // @Param("deptId")
    List<EmployeeDao> findBossByDeptId(@Param("deptId") Integer validOverLapDeptId);

    void insertValidResult(MailResultDao mailResultDao);

    List<MailResultDao> findValidEmail();
}
