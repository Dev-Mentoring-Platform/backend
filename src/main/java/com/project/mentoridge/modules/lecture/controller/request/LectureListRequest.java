package com.project.mentoridge.modules.lecture.controller.request;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import lombok.*;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureListRequest {

    // private String zone;

    private String title;
    private List<String> subjects;      // 언어   / length == 0일 때 전체
    private SystemType systemType;   // 수업방식 : 온라인, 오프라인 / null일 때 전체
    private Boolean isGroup;            // 그룹여부 : 개인, 그룹    / null일 때 전체
    private List<DifficultyType> difficultyTypes; // 레벨 : 입문, 초급, 중급, 고급 / length == 0일 때 전체


    @Builder(access = AccessLevel.PUBLIC)
    private LectureListRequest(String title, List<String> subjects, SystemType systemType, Boolean isGroup, List<DifficultyType> difficultyTypes) {
        this.title = title;
        this.subjects = subjects;
        this.systemType = systemType;
        this.isGroup = isGroup;
        this.difficultyTypes = difficultyTypes;
    }
/*

    public static LectureListRequest of(String title, List<String> subjects, SystemType systemType, Boolean isGroup, List<DifficultyType> difficultyTypes) {
        return LectureListRequest.builder()
                .title(title)
                .subjects(subjects)
                .systemType(systemType)
                .isGroup(isGroup)
                .difficultyTypes(difficultyTypes)
                .build();
    }
*/

//    // TODO - CHECK : -Duplicate
//    @AssertTrue(message = "수업방식 검색이 중복되었습니다.")
//    private boolean isSystemDuplicate() {
//        if (CollectionUtils.isEmpty(systems)) {
//            return true;
//        }
//        Set<SystemType> systemTypeSet = new HashSet<>(systems);
//        return systemTypeSet.size() == systems.size();
//    }
//
//    // TODO - CHECK : -Duplicate
//    @AssertTrue(message = "수업난이도 검색이 중복되었습니다.")
//    private boolean isDifficultyDuplicate() {
//        if (CollectionUtils.isEmpty(difficulties)) {
//            return true;
//        }
//        Set<DifficultyType> difficultyTypeSet = new HashSet<>(difficulties);
//        return difficultyTypeSet.size() == difficulties.size();
//    }

}
