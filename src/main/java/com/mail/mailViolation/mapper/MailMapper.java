package com.mail.mailViolation.mapper;

import com.mail.mailViolation.dto.dao.EmployeeDao;
import com.mail.mailViolation.dto.dao.MailResultDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MailMapper {

    // 지정된 draftsman과 ApprReferYn을 기반으로 EmployeeDao 객체 리스트를 반환하는 메서드
    Optional<EmployeeDao> findByNameAndUseYn(@Param("name") String name);


    // 지정된 deptId를 기반으로 상사(EmployeeDao)의 리스트를 반환하는 메서드
    List<EmployeeDao> findBossByDeptId(@Param("deptId") Integer validOverLapDeptId);

    Optional<String> findSBoss(@Param("deptId") Integer deptId);

    String findBBoss(@Param("deptId") Integer deptId);

    List<String> findTBoss(@Param("deptId") Integer deptId);



    // MailResultDao 객체를 받아서 유효한 결과를 데이터베이스에 삽입하는 메서드
    void insertValidResult(MailResultDao mailResultDao);

    // 유효한 이메일에 대한 결과(MailResultDao 객체 리스트)를 데이터베이스에서 찾아 반환하는 메서드
    List<MailResultDao> searchDateConditionX(Integer fromYear, Integer fromMonth,
                                   Integer toYear, Integer toMonth);
}
