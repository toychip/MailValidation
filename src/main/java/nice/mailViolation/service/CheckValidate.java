package nice.mailViolation.service;

import nice.mailViolation.dto.BossInfo;
import nice.mailViolation.dto.EmployeeDto;
import nice.mailViolation.dto.ReasonIneligibility;
import nice.mailViolation.dto.ConditionAndReasonIneligibility;
import nice.mailViolation.mapper.MailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ConcurrentHashMap<BigDecimal, BossInfo> sBossMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<BigDecimal, BossInfo> bBossMap = new ConcurrentHashMap<>();

    // 적격 Condition setting
    private final ConditionAndReasonIneligibility whenConditionO = ConditionAndReasonIneligibility.builder()
            .condition("O")   // Contiion == O
            .reasonIneligibility(ReasonIneligibility.A) // reasonIneligibility.A 적격
            .build();

    public void loadBossInfoToMemory() {
        List<Map<String, Object>> allTBoss = mapper.findAllTBoss();
        List<BossInfo> allSBoss = mapper.findAllSBoss();
        List<BossInfo> allBBoss = mapper.findAllBBoss();

        // 팀장 정보 로딩
        for (Map<String, Object> map : allTBoss) {
            BigDecimal deptId = (BigDecimal) map.get("DEPT_ID");
            String empName = (String) map.get("EMP_NAME");

            if (!tBossMap.containsKey(deptId)) {
                tBossMap.put(deptId, new ArrayList<>());
            }
            tBossMap.get(deptId).add(empName);
        }

        // 실장 정보 로딩
        for (BossInfo bossInfo : allSBoss) {
            sBossMap.put(bossInfo.getDeptId(), bossInfo);
        }

        // 본부장 정보 로딩
        for (BossInfo bossInfo : allBBoss) {
            bBossMap.put(bossInfo.getDeptId(), bossInfo);
        }
    }

    // 사원 정보 로딩
    public EmployeeDto getEmp(String name) {

        return mapper.findByNameAndUseYn(name)
                .orElse(EmployeeDto.getDefault());
    }

    public List<String> findTBoss(BigDecimal deptId) {
        List<String> newList = new ArrayList<>();
//        log.info("deptId = " + deptId);
        List<String> tBossList = tBossMap.getOrDefault(deptId, new ArrayList<>());
        if (tBossList.isEmpty()) {
            return newList;
        }
        return tBossList;
    }

    public BossInfo findSBoss(BigDecimal deptId) {

        if (deptId == null) {
            deptId = new BigDecimal(-1);
        }
        BossInfo sBossInfo = sBossMap.get(deptId);

        if (sBossInfo == null) {
            return BossInfo.defaultValue();
        }
        return sBossInfo;
    }

    public BossInfo findBBoss(BigDecimal deptId) {
        if (deptId == null) {
            deptId = new BigDecimal(-1);
        }
        BossInfo bBossInfo = bBossMap.get(deptId);
        if (bBossInfo == null) {
            return BossInfo.defaultValue();
        }
        return bBossInfo;
    }

    // 팀장이 결재 했는가?
    public boolean approvalTBoss(BigDecimal deptId, String lastApprover) {
        // 팀장 이름 리스트
        List<String> tBossEmpNameList = findTBoss(deptId);
        for (String s : tBossEmpNameList) {
//            log.info("-------------- 팀장 이름 = " + s);
        }

        // 하나라도 존재하는가?
        return tBossEmpNameList.stream()
                .anyMatch(empName -> empName.equals(lastApprover));
    }

    // 실장, 본부장이 아닌 '일반 사원'일 경우
    public ConditionAndReasonIneligibility basicEmployee(Boolean isTBossApprover, String lastApprover,
                                                         String recipientString, String referenceString,
                                                         String sBossName, String sBossEmail,
                                                         String bBossName, String bBossEmail) {
        String condition;
        ReasonIneligibility reasonIneligibility = ReasonIneligibility.T;    // temp init



        // 팀장이 결재했는가?

        if (isTBossApprover) {
            // 실장 혹은 본부장을 참조했는가?
            boolean isSBossReferer = isSBossReferer(referenceString, sBossName, sBossEmail);
            boolean isBBossReferer = isBBossReferer(referenceString, bBossName, bBossEmail);
            boolean SBReference = isSBossReferer || isBBossReferer;

            // 팀장이 결재 && (실장 or 본부장을 참조)한 경우
            condition = checkCondition(SBReference);

            if (condition.equals("O")) {
                return whenConditionO;
            }

        }

        // 이곳에 왔다면 팀장이 결재하지 않았거나, 팀장이 결재했는데 참조를 올바르게 하지 않은 상태

        // 실장 혹은 본부장이 결재했는가?
        boolean isSBossApprover = isSBossApprover(lastApprover, sBossName);
        boolean isBBossApprover = isBBossApprover(lastApprover, bBossName);

        boolean SBApprover = isSBossApprover || isBBossApprover;
        condition = checkCondition(SBApprover);

        // 팀장이 결재하였지만 부적격이므로 보직좌(실장 혹은 본부장)를 참조하지 않음
        if ((isTBossApprover == true) && (condition.equals("X"))) {
            reasonIneligibility = ReasonIneligibility.C;
        }

        // 팀장과 실장, 본부장 중 아무에게도 결제를 받지 않음
        if ((isTBossApprover == false) && (condition.equals("X"))) {
            reasonIneligibility = ReasonIneligibility.D;
        }

        // 실장 혹은 본부장이 수신처인가?
        boolean isSBosRecipient = isSBosRecipient(recipientString, sBossName, sBossEmail);
        boolean isBBosRecipient = isBBosRecipient(recipientString, bBossName, bBossEmail);

        boolean SBRecipient = isSBosRecipient || isBBosRecipient;
        condition = checkCondition(SBRecipient);


        if (condition.equals("O")) {
            return whenConditionO;
        }

        return ConditionAndReasonIneligibility.builder()
                .condition(condition)
                .reasonIneligibility(reasonIneligibility)
                .build();

    }

    // 실장이 결재했는가?
    public boolean isSBossApprover(String lastApprover, String sBossName) {

        if (lastApprover != null && lastApprover.equals(sBossName)) {
            return true;
        }
        return false;
    }

    // 실장을 참조했는가?
    public boolean isSBossReferer(String refererString, String sBossName, String sBossMail) {
        if (refererString == null) {
            return false;
        }

        // 참조 문자열에서 실장의 이름 또는 이메일이 포함되어 있는지 확인
        if (refererString.contains(sBossName) || refererString.contains(sBossMail)) {
            return true;
        }
        return false;
    }

    // 실장을 수신처로 했는가??
    public boolean isSBosRecipient(String recipientString, String sBossName, String sBossMail) {
        if (recipientString == null) {
            return false;
        }

        // 참조 문자열에서 실장의 이름 또는 이메일이 포함되어 있는지 확인
        if (recipientString.contains(sBossName) || recipientString.contains(sBossMail)) {
            return true;
        }
        return false;
    }


    // 본부장이 결재했는가?
    public boolean isBBossApprover(String lastApprover, String bBossName) {
        if (lastApprover != null && lastApprover.equals(bBossName)) {
            return true;
        }
        return false;
    }

    // 본부장을 참조했는가?
    public boolean isBBossReferer(String refererString, String bBossName, String bBossEmail) {
        if (refererString == null) {
            return false;
        }

        // 참조 문자열에서 본부장의 이름 또는 이메일이 포함되어 있는지 확인
        if (refererString.contains(bBossName) || refererString.contains(bBossEmail)) {
            return true;
        }
        return false;
    }

    // 본부장을 수신처로 했는가?
    public boolean isBBosRecipient(String recipientString, String bBossName, String bBossEmail) {
        if (recipientString == null) {
            return false;
        }

        // 참조 문자열에서 본부장의 이름 또는 이메일이 포함되어 있는지 확인
        if (recipientString.contains(bBossName) || recipientString.contains(bBossEmail)) {
            return true;
        }
        return false;
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
