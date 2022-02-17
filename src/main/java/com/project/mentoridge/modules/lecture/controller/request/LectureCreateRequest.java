package com.project.mentoridge.modules.lecture.controller.request;

import com.project.mentoridge.modules.account.vo.Mentor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LectureCreateRequest extends LectureRequest {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LecturePriceCreateRequest extends LecturePriceRequest {

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
    public static class LectureSubjectCreateRequest extends LectureSubjectRequest {

        @Builder(access = AccessLevel.PUBLIC)
        private LectureSubjectCreateRequest(LearningKindType learningKind, String krSubject) {
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
    private LectureCreateRequest(String title, String subTitle, String introduce, DifficultyType difficulty, String content,
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

    public Lecture toEntity(Mentor mentor) {

        List<LecturePrice> _lecturePrices = new ArrayList<>();
        for (LecturePriceRequest lecturePriceRequest : lecturePrices) {

            if (lecturePriceRequest instanceof LecturePriceCreateRequest) {
                _lecturePrices.add(((LecturePriceCreateRequest) lecturePriceRequest).toEntity(null));
            }
        }

        List<LectureSubject> _lectureSubjects = new ArrayList<>();
        for (LectureSubjectRequest lectureSubjectRequest : lectureSubjects) {

            if (lectureSubjectRequest instanceof LectureSubjectCreateRequest) {
                _lectureSubjects.add(((LectureSubjectCreateRequest) lectureSubjectRequest).toEntity(null));
            }
        }

        return Lecture.builder()
                .mentor(mentor)
                .title(title)
                .subTitle(subTitle)
                .introduce(introduce)
                .content(content)
                .difficulty(difficulty)
                .systems(systems)
                .thumbnail(thumbnail)
                .lecturePrices(_lecturePrices)
                .lectureSubjects(_lectureSubjects)
                .build();
    }
}
