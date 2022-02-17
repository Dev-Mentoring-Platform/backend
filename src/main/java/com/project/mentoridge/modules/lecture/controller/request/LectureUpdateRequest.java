package com.project.mentoridge.modules.lecture.controller.request;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
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
public class LectureUpdateRequest extends LectureRequest {

//    @Valid
//    // @Length(min = 1, max = 5, message = "강의방식2는 최소 {min}개 ~ 최대 {max}개만 선택할 수 있습니다.")
//    @NotNull(message = "강의방식2를 입력해주세요.")
//    private List<LecturePriceUpdateRequest> lecturePrices;
//
//    @Valid
//    // @Length(min = 1, message = "강의종류를 최소 1개 입력해주세요.")
//    @NotNull(message = "강의종류를 입력해주세요.")
//    private List<LectureSubjectUpdateRequest> lectureSubjects;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LecturePriceUpdateRequest extends LecturePriceRequest {

        @Builder(access = AccessLevel.PUBLIC)
        private LecturePriceUpdateRequest(Boolean isGroup, Integer numberOfMembers,
                                          Long pricePerHour, Integer timePerLecture, Integer numberOfLectures, Long totalPrice) {
            this.isGroup = isGroup;
            this.numberOfMembers = numberOfMembers;
            this.pricePerHour = pricePerHour;
            this.timePerLecture = timePerLecture;
            this.numberOfLectures = numberOfLectures;
            this.totalPrice = totalPrice;
        }

        @Override
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
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LectureSubjectUpdateRequest extends LectureSubjectRequest {

        @Builder(access = AccessLevel.PUBLIC)
        private LectureSubjectUpdateRequest(LearningKindType learningKind, String krSubject) {
            this.learningKind = learningKind;
            this.krSubject = krSubject;
        }

        @Override
        public LectureSubject toEntity(Lecture lecture) {
            return LectureSubject.builder()
                    .lecture(lecture)
                    .learningKind(learningKind)
                    .krSubject(krSubject)
                    .build();
        }
    }

    @Builder(access = AccessLevel.PUBLIC)
    private LectureUpdateRequest(String title, String subTitle, String introduce, DifficultyType difficulty, String content,
                                 List<SystemType> systems, List<LectureRequest.LecturePriceRequest> lecturePrices, List<LectureRequest.LectureSubjectRequest> lectureSubjects, String thumbnail) {
        this.title = title;
        this.subTitle = subTitle;
        this.introduce = introduce;
        this.difficulty = difficulty;
        this.content = content;
        this.systems = systems;
        this.lecturePrices = lecturePrices;
        this.lectureSubjects = lectureSubjects;
        this.thumbnail = thumbnail;
    }
}
