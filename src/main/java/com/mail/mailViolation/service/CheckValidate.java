package com.mail.mailViolation.service;

import com.mail.mailViolation.dto.EmployeeDto;
import com.mail.mailViolation.exception.NotFoundTBossException;
import com.mail.mailViolation.mapper.MailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckValidate {

    private final MailMapper mapper;

    // 멤버 변수 선언
    private final ConcurrentHashMap<BigDecimal, List<String>> tBossMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<BigDecimal, String> sBossMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<BigDecimal, String> bBossMap = new ConcurrentHashMap<>();

    public void loadBossInfoToMemory() {
        List<Map<String, Object>> allTBoss = mapper.findAllTBoss();
        List<Map<String, Object>> allSBoss = mapper.findAllSBoss();
        List<Map<String, Object>> allBBoss = mapper.findAllBBoss();

        for (Map<String, Object> map : allTBoss) {
            BigDecimal deptId = (BigDecimal) map.get("DEPT_ID");
            String empName = (String) map.get("EMP_NAME");

//            log.info("----- loadBossInfoToMemory. deptId = " + deptId);
//            log.info("deptId.getClass() = " + deptId.getClass());

            if (!tBossMap.containsKey(deptId)) {
                tBossMap.put(deptId, new ArrayList<>());
            }
            tBossMap.get(deptId).add(empName);
        }

        for (Map<String, Object> map : allSBoss) {
            sBossMap.put((BigDecimal) map.get("DEPT_ID"), (String) map.get("EMP_NAME"));
        }

        for (Map<String, Object> map : allBBoss) {
            BigDecimal deptId = (BigDecimal) map.get("DEPT_ID");
            String empName = (String) map.get("EMP_NAME");

            // 상위 부서의 본부장을 저장
            bBossMap.put(deptId, empName);
        }
    }

    public EmployeeDto getEmp(String name) {

        return mapper.findByNameAndUseYn(name)
                .orElse(EmployeeDto.getDefault());
    }

    // 팀장이 결재 후 실장 혹은 본부장이을 참초 or 결재 했는가?
    public boolean matchSBBoss(String exelEmpName, String sBossEmpName, String bBossEmpName) {
        return matchBoss(exelEmpName, sBossEmpName) || matchBoss(exelEmpName, bBossEmpName);
    }

    // 팀장이 결재 했는가?
    public boolean approvalTBoss(String lastApprover,
                                 BigDecimal deptId) {
        // 팀장 이름 리스트
        List<String> tBossEmpNameList = findTBoss(deptId);
        for (String s : tBossEmpNameList) {
//            log.info("-------------- 팀장 이름 = " + s);
        }

        // 하나라도 존재하는가?
        return tBossEmpNameList.stream()
                .anyMatch(empName -> empName.equals(lastApprover));
    }

    // 실장, 본부장 - 결재 혹은 참조로 match 확인
    public boolean matchBoss(String exelEmpName,
                             String realEmpName) {

        if (exelEmpName.contains(realEmpName)) {
            return true;
        }
        return false;
    }

    public List<String> findTBoss(BigDecimal deptId) {
//        log.info("deptId = " + deptId);
        List<String> tBossList = tBossMap.getOrDefault(deptId, new ArrayList<>());
        if (tBossList.isEmpty()) {
            throw new NotFoundTBossException("팀장을 찾을 수 없음");
        }
        return tBossList;
    }

    public String findSBoss(BigDecimal deptId) {
        if (deptId == null) {
            return "DeptId가 null입니다";
        }
        return sBossMap.getOrDefault(deptId, "실장이 존재하지 않으니 IT혁신실이지?");
    }

    public String findBBoss(BigDecimal deptId) {
//        log.info("findBBoss.deptId = " + deptId);
        if (deptId == null) {
            return "DeptId가 null입니다";
        }
        
        
        return bBossMap.getOrDefault(deptId, "본부장이 존재하지 않음");
    }

    // 팀장, 실장, 본부장이 아닌 '일반 사원'일 경우
    public String basicEmployee(String lastApprover, BigDecimal empDeptId, String referencer,
                                String sBossEmpName, String bBossEmpName) {
        String condition;

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
        // 이곳에 왔다면 팀장이 결재하지 않았거나, 팀장이 결재했는데 참조를 올바르게 하지 않은 상태

        // 실장 혹은 본부장이 결재했는가?
        boolean approvalSBBoss = matchSBBoss(lastApprover, sBossEmpName, bBossEmpName);
        condition = checkCondition(approvalSBBoss);

        return condition;
    }

    // if true -> condition == O
    public String checkCondition(boolean trueOrFalse) {
//        log.info("trueOrFalse = " + trueOrFalse);
        if (trueOrFalse) {
            return "O";
        }
        return "X";
    }
}
