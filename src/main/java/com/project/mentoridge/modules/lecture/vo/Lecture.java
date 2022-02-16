package com.project.mentoridge.modules.lecture.vo;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.*;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
//@Setter
@NoArgsConstructor(access = PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "lecture_id"))
@Entity
@Table(name = "lecture")
public class Lecture extends BaseEntity {

    // 단방향
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id",
                referencedColumnName = "mentor_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_LECTURE_MENTOR_ID"))
    private Mentor mentor;

    @Column(nullable = false, length = 40)
    private String title;

    @Column(nullable = false, length = 25)
    private String subTitle;

    @Column(nullable = false, length = 25)
    private String introduce;

    @Lob
    @Column(nullable = false, length = 25)
    private String content;

    @Column(nullable = false, length = 20)
    private DifficultyType difficulty;

    // TODO - CHECK : prohannah.tistory.com/133
    @ElementCollection(targetClass = SystemType.class, fetch = FetchType.LAZY)
    @CollectionTable(
            name = "lecture_system_type",
            joinColumns = @JoinColumn(name = "lecture_id",
                    nullable = false,
                    referencedColumnName = "lecture_id",
                    foreignKey = @ForeignKey(name = "FK_LECTURE_SYSTEM_TYPE_LECTURE_ID"))
    )   // cascade = CascadeType.ALL
    private List<SystemType> systems = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LecturePrice> lecturePrices = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureSubject> lectureSubjects = new ArrayList<>();

    private String thumbnail;

    public void addSubject(LectureSubject lectureSubject) {
        lectureSubjects.add(lectureSubject);
        lectureSubject.mappingLecture(this);
    }

    public void addPrice(LecturePrice lecturePrice) {
        lecturePrices.add(lecturePrice);
        lecturePrice.mappingLecture(this);
    };

    public void addEnrollment(Enrollment enrollment) {
        enrollment.setLecture(this);
    }

    @Builder(access = PUBLIC)
    private Lecture(Mentor mentor, String title, String subTitle, String introduce, String content, DifficultyType difficulty, List<SystemType> systems, String thumbnail) {
        this.mentor = mentor;
        this.title = title;
        this.subTitle = subTitle;
        this.introduce = introduce;
        this.content = content;
        this.difficulty = difficulty;
        this.systems = systems;
        this.thumbnail = thumbnail;

        this.lecturePrices = new ArrayList<>();
        this.lectureSubjects = new ArrayList<>();
    }
/*
    public static Lecture of(Mentor mentor, String title, String subTitle, String introduce, String content, DifficultyType difficultyType, List<SystemType> systemTypes, String thumbnail) {
        return Lecture.builder()
                .mentor(mentor)
                .title(title)
                .subTitle(subTitle)
                .introduce(introduce)
                .content(content)
                .difficultyType(difficultyType)
                .systemTypes(systemTypes)
                .thumbnail(thumbnail)
                .build();
    }*/

    public void update(LectureUpdateRequest lectureUpdateRequest) {

        this.getLecturePrices().clear();
        this.getLectureSubjects().clear();

        for (LectureUpdateRequest.LecturePriceUpdateRequest lecturePriceUpdateRequest : lectureUpdateRequest.getLecturePrices()) {
            this.addPrice(lecturePriceUpdateRequest.toEntity(this));
        }

        for (LectureUpdateRequest.LectureSubjectUpdateRequest lectureSubjectUpdateRequest : lectureUpdateRequest.getSubjects()) {
            this.addSubject(lectureSubjectUpdateRequest.toEntity(this));
        }

        this.thumbnail = lectureUpdateRequest.getThumbnailUrl();
        this.title = lectureUpdateRequest.getTitle();
        this.subTitle = lectureUpdateRequest.getSubTitle();
        this.introduce = lectureUpdateRequest.getIntroduce();
        this.content = lectureUpdateRequest.getContent();
        this.difficulty = lectureUpdateRequest.getDifficulty();
        this.systems = lectureUpdateRequest.getSystems();
    }

    private static LectureSubject buildLectureSubject(LectureCreateRequest.LectureSubjectCreateRequest lectureSubjectCreateRequest) {
        return LectureSubject.of(
                null,
                lectureSubjectCreateRequest.getLearningKindId(),
                lectureSubjectCreateRequest.getLearningKind(),
                lectureSubjectCreateRequest.getKrSubject()
        );
    }

    private static LecturePrice buildLecturePrice(LectureCreateRequest.LecturePriceCreateRequest lecturePriceCreateRequest) {
        return lecturePriceCreateRequest.toEntity(null);
    }

}
