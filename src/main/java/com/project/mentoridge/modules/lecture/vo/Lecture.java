package com.project.mentoridge.modules.lecture.vo;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;

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

    // TODO - 관리자 승인 기능 추가
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean approved = false;

    // TODO - 강의 종료
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean closed = false;

    @Builder(access = PUBLIC)
    private Lecture(Mentor mentor, String title, String subTitle, String introduce, String content, DifficultyType difficulty,
                    List<SystemType> systems, String thumbnail, List<LecturePrice> lecturePrices, List<LectureSubject> lectureSubjects) {
        this.mentor = mentor;
        this.title = title;
        this.subTitle = subTitle;
        this.introduce = introduce;
        this.content = content;
        this.difficulty = difficulty;
        this.systems = systems;
        this.thumbnail = thumbnail;
        if (lecturePrices != null) {
            lecturePrices.forEach(this::addPrice);
        }
        if (lectureSubjects != null) {
            lectureSubjects.forEach(this::addSubject);
        }
    }

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

    public void update(LectureUpdateRequest lectureUpdateRequest) {

        this.getLecturePrices().clear();
        this.getLectureSubjects().clear();

        this.title = lectureUpdateRequest.getTitle();
        this.subTitle = lectureUpdateRequest.getSubTitle();
        this.introduce = lectureUpdateRequest.getIntroduce();
        this.content = lectureUpdateRequest.getContent();
        this.difficulty = lectureUpdateRequest.getDifficulty();
        this.systems = lectureUpdateRequest.getSystems();
        this.thumbnail = lectureUpdateRequest.getThumbnail();
    }

    public void approve() {
        if (isApproved()) {
            throw new RuntimeException("이미 승인된 강의입니다.");
        }
        this.approved = true;
    }

    public void cancelApproval() {
        this.approved = false;
    }

    // TODO - 멘토 : 강의 종료
    public void close() {
        this.closed = true;
    }

    public void open() {
        this.closed = false;
    }

}
