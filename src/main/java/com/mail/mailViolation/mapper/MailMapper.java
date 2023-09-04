package com.mail.mailViolation.mapper;

import com.mail.mailViolation.dto.EmployeeDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MailMapper {

//    @Select("SELECT EMP_ID, EMP_NAME, DEPT_ID, EMP_EMAIL, APPR_REFER_YN" +
//            " FROM TB_AS_SCRT_EMP" +
//            " WHERE EMP_NAME = #{draftsman}" +
//            " AND APPR_REFER_YN = 'N'")
    List<EmployeeDao> findEmployeeByDraftsmanAndApprReferYn(@Param("draftsman") String draftsman);

}
