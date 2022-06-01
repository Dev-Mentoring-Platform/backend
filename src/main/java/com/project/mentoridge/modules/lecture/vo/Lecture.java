package com.project.mentoridge.modules.lecture.vo;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.SUBJECT;
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

    private void update(LectureUpdateRequest lectureUpdateRequest, SubjectRepository subjectRepository) {

        this.getLecturePrices().clear();
        this.getLectureSubjects().clear();

        this.title = lectureUpdateRequest.getTitle();
        this.subTitle = lectureUpdateRequest.getSubTitle();
        this.introduce = lectureUpdateRequest.getIntroduce();
        this.content = lectureUpdateRequest.getContent();
        this.difficulty = lectureUpdateRequest.getDifficulty();
        this.systems = lectureUpdateRequest.getSystems();
        this.thumbnail = lectureUpdateRequest.getThumbnail();

        for (LectureUpdateRequest.LecturePriceUpdateRequest lecturePriceUpdateRequest : lectureUpdateRequest.getLecturePrices()) {
            this.addPrice(lecturePriceUpdateRequest.toEntity(null));
        }
        for (LectureUpdateRequest.LectureSubjectUpdateRequest lectureSubjectUpdateRequest : lectureUpdateRequest.getLectureSubjects()) {
            Subject subject = subjectRepository.findById(lectureSubjectUpdateRequest.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException(SUBJECT));
            LectureSubject lectureSubject = LectureSubject.builder()
                    .lecture(null)
                    .subject(subject)
                    .build();
            this.addSubject(lectureSubject);
        }
    }

    public void update(LectureUpdateRequest lectureUpdateRequest,  SubjectRepository subjectRepository, User user, LectureLogService lectureLogService) {

        Lecture before = this.copy();
        update(lectureUpdateRequest, subjectRepository);

        // 수정된 강의는 재승인 필요
        this.cancelApproval();
        lectureLogService.update(user, before, this);
    }

    public void delete(User user, LectureLogService lectureLogService) {
        lectureLogService.delete(user, this);
    }

    public void approve(LectureLogService lectureLogService) {
        if (isApproved()) {
            throw new RuntimeException("이미 승인된 강의입니다.");
        }
        this.approved = true;
        lectureLogService.approve(this);
    }

    public void cancelApproval() {
        this.approved = false;
    }

    private Lecture copy() {
        return Lecture.builder()
                .mentor(mentor)
                .title(title)
                .subTitle(subTitle)
                .introduce(introduce)
                .content(content)
                .difficulty(difficulty)
                .thumbnail(thumbnail)
                .systems(systems)
                .lecturePrices(lecturePrices)
                .lectureSubjects(lectureSubjects)
                .build();
    }

}
