package com.project.mentoridge.modules.lecture.controller.request;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LectureCreateRequest {

    @GroupSequence({OrderFirst.class, OrderSecond.class})
    public interface Order {}
    public interface OrderFirst {}
    public interface OrderSecond {}

    @NotBlank(message = "강의 소개 메인 이미지를 입력해주세요.", groups = OrderFirst.class)
    private String thumbnailUrl;

    @Length(min = 1, max = 40, message = "제목을 {min}자 ~ {max}자 이내로 입력해주세요.", groups = OrderFirst.class)
    @NotBlank(message = "강의 타이틀을 입력해주세요.", groups = OrderFirst.class)
    private String title;

    @Length(min = 1, max = 25, message = "강의 소제목을 {min}자 ~ {max}자 이내로 입력해주세요.", groups = OrderFirst.class)
    @NotBlank(message = "강의 소제목을 입력해주세요.", groups = OrderFirst.class)
    private String subTitle;

    @Length(min = 1, max = 200, message = "내 소개를 {min}자 ~ {max}자 이내로 입력해주세요.", groups = OrderFirst.class)
    @NotBlank(message = "내 소개를 입력해주세요.", groups = OrderFirst.class)
    private String introduce;

    @NotNull(message = "난이도를 입력해주세요.", groups = OrderFirst.class)
    private DifficultyType difficulty;

    @NotBlank(message = "강의 상세내용을 입력해주세요.", groups = OrderFirst.class)
    private String content;

    @NotNull(message = "강의방식1을 입력해주세요.", groups = OrderFirst.class)
    private List<SystemType> systems;

    @Valid
    @Length(min = 1, max = 5, message = "강의방식2는 최소 {min}개 ~ 최대 {max}개만 선택할 수 있습니다.")
    @NotNull(message = "강의방식2를 입력해주세요.")
    private List<LecturePriceCreateRequest> lecturePrices;

    @Valid
    @Length(min = 1, message = "강의종류를 최소 1개 입력해주세요.")
    @NotNull(message = "강의종류를 입력해주세요.")
    private List<LectureSubjectCreateRequest> subjects;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LectureSubjectCreateRequest {

        @NotNull(message = "강의 종류를 선택해주세요.")
        private Long learningKindId;

        @NotBlank(message = "강의 종류를 선택해주세요.")
        private String learningKind;

        @NotBlank(message = "과목을 입력해주세요.")
        private String krSubject;

//        @Builder(access = AccessLevel.PUBLIC)
//        private LectureSubjectCreateRequest(Long learningKindId, String learningKind, String krSubject) {
//            this.learningKindId = learningKindId;
//            this.learningKind = learningKind;
//            this.krSubject = krSubject;
//        }
        @Builder(access = AccessLevel.PUBLIC)
        private LectureSubjectCreateRequest(LearningKindType learningKind, String krSubject) {
            this.learningKindId = learningKind.getId();
            this.learningKind = learningKind.getName();
            this.krSubject = krSubject;
        }

//        public static LectureSubjectCreateRequest of(Long learningKindId, String learningKind, String krSubject) {
//            return LectureSubjectCreateRequest.builder()
//                    .learningKindId(learningKindId)
//                    .learningKind(learningKind)
//                    .krSubject(krSubject)
//                    .build();
//        }
//
//        public static LectureSubjectCreateRequest of(LearningKindType type, String krSubject) {
//            return LectureSubjectCreateRequest.builder()
//                    .learningKindId(type.getId())
//                    .learningKind(type.getName())
//                    .krSubject(krSubject)
//                    .build();
//        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LecturePriceCreateRequest {

        @NotNull(message = "그룹여부를 선택해주세요.", groups = OrderFirst.class)
        private Boolean isGroup;

        private Integer numberOfMembers;

        @NotNull(message = "시간당 수강료를 입력해주세요.", groups = OrderFirst.class)
        private Long pricePerHour;

        @NotNull(message = "1회당 강의 시간을 입력해주세요.", groups = OrderFirst.class)
        private Integer timePerLecture;

        @NotNull(message = "강의 횟수를 입력해주세요.", groups = OrderFirst.class)
        private Integer numberOfLectures;

        @NotNull(message = "최종 수강료를 입력해주세요.", groups = OrderFirst.class)
        private Long totalPrice;

        @AssertTrue(message = "그룹 수업 인원수를 입력해주세요.", groups = OrderSecond.class)
        private boolean isGroupNumber() {
            if (Boolean.TRUE.equals(isGroup)) {
                return !Objects.isNull(numberOfMembers) && numberOfMembers > 0;
            }
            return true;
        }

        @Builder(access = AccessLevel.PUBLIC)
        private LecturePriceCreateRequest(Boolean isGroup, Integer numberOfMembers,
                                          Long pricePerHour, Integer timePerLecture, Integer numberOfLectures, Long totalPrice) {
            this.isGroup = isGroup;
            this.numberOfMembers = numberOfMembers;
            this.pricePerHour = pricePerHour;
            this.timePerLecture = timePerLecture;
            this.numberOfLectures = numberOfLectures;
            this.totalPrice = totalPrice;
        }

        public LecturePrice toEntity(Lecture lecture) {
            return LecturePrice.builder()
                    .lecture(lecture)
                    .isGroup(isGroup)
                    .numberOfMembers(numberOfMembers)
                    .pricePerHour(pricePerHour)
                    .timePerLecture(timePerLecture)
                    .numberOfLectures(numberOfLectures)
                    .totalPrice(totalPrice)
                    .build();
        }

/*        public static LecturePriceCreateRequest of(Boolean isGroup, Integer groupNumber, Long pertimeCost, Integer pertimeLecture, Integer totalTime, Long totalCost) {
            return LecturePriceCreateRequest.builder()
                    .isGroup(isGroup)
                    .groupNumber(groupNumber)
                    .pertimeCost(pertimeCost)
                    .pertimeLecture(pertimeLecture)
                    .totalTime(totalTime)
                    .totalCost(totalCost)
                    .build();
        }*/
    }

    @Builder(access = AccessLevel.PUBLIC)
    private LectureCreateRequest(String thumbnailUrl, String title, String subTitle, String introduce, DifficultyType difficulty, String content, List<SystemType> systems, List<LecturePriceCreateRequest> lecturePrices, List<LectureSubjectCreateRequest> subjects) {
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.subTitle = subTitle;
        this.introduce = introduce;
        this.difficulty = difficulty;
        this.content = content;
        this.systems = systems;
        this.lecturePrices = lecturePrices;
        this.subjects = subjects;
    }

/*    public static LectureCreateRequest of(String thumbnailUrl, String title, String subTitle, String introduce, DifficultyType difficulty, String content, List<SystemType> systems, List<LecturePriceCreateRequest> lecturePrices, List<LectureSubjectCreateRequest> subjects) {
        return LectureCreateRequest.builder()
                .thumbnailUrl(thumbnailUrl)
                .title(title)
                .subTitle(subTitle)
                .introduce(introduce)
                .difficulty(difficulty)
                .content(content)
                .systems(systems)
                .lecturePrices(lecturePrices)
                .subjects(subjects)
                .build();
    }*/
}
