package nice.mailViolation.mapper;

import nice.mailViolation.dto.BossInfo;
import nice.mailViolation.dto.EmployeeDto;
import nice.mailViolation.dto.MailResultDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface MailMapper {

    // 지정된 draftsman과 ApprReferYn을 기반으로 EmployeeDao 객체 리스트를 반환하는 메서드
    Optional<EmployeeDto> findByNameAndUseYn(@Param("name") String name);

    // MailResultDao 객체를 받아서 유효한 결과를 데이터베이스에 삽입하는 메서드
    void insertValidResult(MailResultDto mailResultDto);

    // 유효한 이메일에 대한 결과(MailResultDao 객체 리스트)를 데이터베이스에서 찾아 반환하는 메서드
    List<MailResultDto> searchDateConditionX(Integer fromYear, Integer fromMonth,
                                             Integer toYear, Integer toMonth);
    List<Map<String, Object>> findAllTBoss();
    List<BossInfo> findAllSBoss();
    List<BossInfo> findAllBBoss();

}
