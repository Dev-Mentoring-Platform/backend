package com.project.mentoridge.modules.lecture.controller.request;

import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import org.hibernate.validator.constraints.Length;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public abstract class LectureRequest {

    @GroupSequence({OrderFirst.class, OrderSecond.class})
    public interface Order {}
    public interface OrderFirst {}
    public interface OrderSecond {}

    @Length(min = 1, max = 40, message = "제목을 {min}자 ~ {max}자 이내로 입력해주세요.", groups = OrderFirst.class)
    @NotBlank(message = "강의 타이틀을 입력해주세요.", groups = OrderFirst.class)
    protected String title;

    @Length(min = 1, max = 25, message = "강의 소제목을 {min}자 ~ {max}자 이내로 입력해주세요.", groups = OrderFirst.class)
    @NotBlank(message = "강의 소제목을 입력해주세요.", groups = OrderFirst.class)
    protected String subTitle;

    @Length(min = 1, max = 200, message = "내 소개를 {min}자 ~ {max}자 이내로 입력해주세요.", groups = OrderFirst.class)
    @NotBlank(message = "내 소개를 입력해주세요.", groups = OrderFirst.class)
    protected String introduce;

    @NotBlank(message = "강의 상세내용을 입력해주세요.", groups = OrderFirst.class)
    protected String content;

    @NotNull(message = "난이도를 입력해주세요.", groups = OrderFirst.class)
    protected DifficultyType difficulty;

    @NotNull(message = "강의방식1을 입력해주세요.", groups = OrderFirst.class)
    protected List<SystemType> systems;

    @Valid
    //@Length(min = 1, max = 5, message = "강의방식2는 최소 {min}개 ~ 최대 {max}개만 선택할 수 있습니다.")
    @NotNull(message = "강의방식2를 입력해주세요.")
    protected List<LecturePriceRequest> lecturePrices;

    @Valid
    //@Length(min = 1, message = "강의종류를 최소 1개 입력해주세요.")
    @NotNull(message = "강의종류를 입력해주세요.")
    protected List<LectureSubjectRequest> subjects;

    @NotBlank(message = "강의 소개 메인 이미지를 입력해주세요.", groups = OrderFirst.class)
    protected String thumbnail;

    protected abstract static class LectureSubjectRequest {

//        @NotNull(message = "강의 종류를 선택해주세요.")
//        protected Long learningKindId;
//        @NotBlank(message = "강의 종류를 선택해주세요.")
//        protected String learningKind;
        @NotNull(message = "강의 종류를 선택해주세요.")
        protected LearningKindType learningKind;

        @NotBlank(message = "과목을 입력해주세요.")
        protected String krSubject;
    }

    protected abstract static class LecturePriceRequest {

        @NotNull(message = "그룹여부를 선택해주세요.", groups = OrderFirst.class)
        protected Boolean isGroup;

        protected Integer numberOfMembers;

        @NotNull(message = "시간당 수강료를 입력해주세요.", groups = OrderFirst.class)
        protected Long pricePerHour;

        @NotNull(message = "1회당 강의 시간을 입력해주세요.", groups = OrderFirst.class)
        protected Integer timePerLecture;

        @NotNull(message = "강의 횟수를 입력해주세요.", groups = OrderFirst.class)
        protected Integer numberOfLectures;

        @NotNull(message = "최종 수강료를 입력해주세요.", groups = OrderFirst.class)
        protected Long totalPrice;

//        @AssertTrue(message = "그룹 수업 인원수를 입력해주세요.", groups = OrderSecond.class)
//        private boolean isGroupNumber() {
//            if (Boolean.TRUE.equals(isGroup)) {
//                return !Objects.isNull(numberOfMembers) && numberOfMembers > 0;
//            }
//            return true;
//        }

        protected abstract LecturePrice toEntity(Lecture lecture);

    }

}
