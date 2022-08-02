package com.project.mentoridge.modules.lecture.controller;

import com.project.mentoridge.modules.base.AbstractRequestTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LectureListRequestTest extends AbstractRequestTest {
    // TODO - CHECK

//    @ParameterizedTest
//    @MethodSource("generateClearRequest")
//    void 완벽한파라미터_테스트(List<String> parents, List<String> subjects, List<SystemType> systemTypes, List<DifficultyType> difficultyTypes, Boolean isGroup) {
//        LectureListRequest request = LectureListRequest.of(null, parents, subjects, systemTypes, isGroup, difficultyTypes);
//        Set<ConstraintViolation<LectureListRequest>> validate = validator.validate(request);
//        assertTrue(validate.size() == 0);
//    }
//
//    @ParameterizedTest
//    @MethodSource("generateDuplicateRequest")
//    void 중복파라미터_테스트(List<String> parents, List<String> subjects, List<SystemType> systemTypes, List<DifficultyType> difficultyTypes, Boolean isGroup) {
//        LectureListRequest request = LectureListRequest.of(null, parents, subjects, systemTypes, isGroup, difficultyTypes);
//        Set<ConstraintViolation<LectureListRequest>> validate = validator.validate(request);
//        assertTrue(validate.size() > 0);
//        validate.forEach(validation -> {
//            System.out.println(validation.getConstraintDescriptor().getMessageTemplate());
//            assertFalse(Boolean.getBoolean(validation.getInvalidValue().toString()));
//        });
//    }
//
//    static Stream<Arguments> generateClearRequest() {
//        List<String> parents = Arrays.asList("개발", "프로그래밍언어");
//        List<String> subjects = Arrays.asList("자바", "백엔드", "프론트");
//        List<SystemType> systemTypes = Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE, SystemType.NEGOTIABLE);
//        List<DifficultyType> difficultyTypes = Arrays.asList(DifficultyType.BEGINNER, DifficultyType.ADVANCED, DifficultyType.BASIC, DifficultyType.INTERMEDIATE);
//
//        return Stream.of(
//                Arguments.of(parents, subjects, systemTypes, difficultyTypes, true),
//                Arguments.of(null, null, null, null, null)
//        );
//    }
//
//    static Stream<Arguments> generateDuplicateRequest() {
//        List<SystemType> systemTypes = Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE, SystemType.NEGOTIABLE, SystemType.NEGOTIABLE);
//        List<DifficultyType> difficultyTypes = Arrays.asList(DifficultyType.BEGINNER, DifficultyType.ADVANCED, DifficultyType.BASIC, DifficultyType.INTERMEDIATE, DifficultyType.INTERMEDIATE);
//
//        return Stream.of(
//                Arguments.of(null, null, systemTypes, null, true),
//                Arguments.of(null, null, null, difficultyTypes, true)
//        );
//    }

}
