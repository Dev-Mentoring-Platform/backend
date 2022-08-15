package com.project.mentoridge.modules.review.vo;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.MenteeReviewLogService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "mentee_review_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter(AccessLevel.PRIVATE)
@Entity
public class MenteeReview extends BaseEntity {

    private Integer score;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id",
            referencedColumnName = "mentee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENTEE_REVIEW_MENTEE_ID"))
    private Mentee mentee;

    // OneToOne
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id",
            referencedColumnName = "enrollment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENTEE_REVIEW_ENROLLMENT_ID"))
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id",
            referencedColumnName = "lecture_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENTEE_REVIEW_LECTURE_ID"))
    private Lecture lecture;

    // https://tecoble.techcourse.co.kr/post/2021-08-15-jpa-cascadetype-remove-vs-orphanremoval-true/
    /*
    In Hibernate, you cannot overwrite a collection retrieved from the persistence context
    if the association has orphanRemoval = true specified.
    If your goal is to end up with an empty collection, use p.getPhones().clear() instead.
    */
    //@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<MentorReview> children = new ArrayList<>();

    @Builder(access = AccessLevel.PUBLIC)
    private MenteeReview(Integer score, String content, Mentee mentee, Enrollment enrollment, Lecture lecture) {
        this.score = score;
        this.content = content;
        this.mentee = mentee;
        this.enrollment = enrollment;
        this.lecture = lecture;
    }

    public void addChild(MentorReview child) {
        this.children.add(child);
        child.setParent(this);
    }

    public void delete(User user, MenteeReviewLogService menteeReviewLogService) {
        menteeReviewLogService.delete(user, this);
    }

    public void update(MenteeReviewUpdateRequest menteeReviewUpdateRequest, User user, MenteeReviewLogService menteeReviewLogService) {

        MenteeReview before = this.copy();

        setScore(menteeReviewUpdateRequest.getScore());
        setContent(menteeReviewUpdateRequest.getContent());
        menteeReviewLogService.update(user, before, this);
    }

    private MenteeReview copy() {
        return MenteeReview.builder()
                .score(score)
                .content(content)
                .mentee(mentee)
                .enrollment(enrollment)
                .lecture(lecture)
                .build();
    }
}
