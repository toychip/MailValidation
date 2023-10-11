package com.mail.mailViolation.service;

import com.mail.mailViolation.dto.dao.EmployeeDao;
import com.mail.mailViolation.mapper.MailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckValidate {

    private final MailMapper mapper;

    // Y, N 등 여러개의 리스트 반환시 가장 최신 것으로 반환하는 메서드
    public EmployeeDao getEmp(String name) {

        return mapper.findByNameAndUseYn(name)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));
    }

    // 팀장이 결재 후 실장 혹은 본부장이을 참초 or 결재 했는가?
    public boolean matchSBBoss(String exelEmpName, String sBossEmpName, String bBossEmpName) {
        return matchBoss(exelEmpName, sBossEmpName) || matchBoss(exelEmpName, bBossEmpName);
    }

    // 팀장이 결재 했는가?
    public boolean approvalTBoss(String lastApprover,
                                 Integer deptId) {
        // 팀장 이름 리스트
        List<String> tBossEmpNameList = findTBoss(deptId);

        // 하나라도 존재하는가?
        return tBossEmpNameList.stream()
                .anyMatch(empName -> empName.equals(lastApprover));
    }

    // 실장, 본부장 - 결재 혹은 참조로 match 확인
    public boolean matchBoss(String exelEmpName,
                             String realEmpName) {

        if (exelEmpName.equals(realEmpName)) {
            return true;
        }
        return false;
    }

    // deptId로 팀장 이름 찾기
    public List<String> findTBoss(Integer deptId) {
        return mapper.findTBoss(deptId)
                .orElseThrow(() -> new RuntimeException("팀장을 찾을 수 없음"));
    }

    // deptId로 실장 이름 찾기
    public String findSBoss(Integer deptId) {
        return mapper.findSBoss(deptId)
                .orElseThrow(() -> new RuntimeException("실장을 찾을 수 없음"));
    }

    // deptId로 본부장 이름 찾기
    public String findBBoss(Integer deptId){
        return mapper.findBBoss(deptId);
    }

    // 팀장, 실장, 본부장이 아닌 '일반 사원'일 경우
    public String basicEmployee(String lastApprover, Integer empDeptId, String referencer,
                                String sBossEmpName, String bBossEmpName) {
        String condition = "X";

        // 팀장이 결재했는가?
        boolean isApprovalTBoss = approvalTBoss(lastApprover, empDeptId);
        if (isApprovalTBoss) {
            // 실장 혹은 본부장을 참조했는가?
            boolean referenceSBBoss = matchSBBoss(referencer, sBossEmpName, bBossEmpName);

            // 팀장이 결재 && (실장 or 본부장을 참조)한 경우
            condition = checkCondition(referenceSBBoss);
            if (condition.equals("O")) {
                return condition;
            }
        }

        // 실장 혹은 본부장이 결재했는가?
        boolean approvalSBBoss = matchSBBoss(lastApprover, sBossEmpName, bBossEmpName);
        condition = checkCondition(approvalSBBoss);

        return condition;
    }

    // if true -> condition == O
    public String checkCondition(boolean trueOrFalse) {
        if (trueOrFalse) {
            return "O";
        }
        return "X";
    }
}
