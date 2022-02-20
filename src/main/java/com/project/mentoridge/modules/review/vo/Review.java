package com.project.mentoridge.modules.review.vo;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "review_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter(AccessLevel.PRIVATE)
@Entity
public class Review extends BaseEntity {

    private Integer score;
    private String content;

    // 단방향
    // TODO - CHECK : 양방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_REVIEW_USER_ID"))
    private User user;  // TODO - CHECK : User or Mentee/Mentor

    // OneToOne
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id",
            referencedColumnName = "enrollment_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "FK_REVIEW_ENROLLMENT_ID"))
    private Enrollment enrollment;

    // 단방향
    // TODO - CHECK : 양방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id",
            referencedColumnName = "lecture_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_REVIEW_LECTURE_ID"))
    private Lecture lecture;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id",
            referencedColumnName = "review_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "FK_REVIEW_PARENT_ID"))
    private Review parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> children = new ArrayList<>();

    @Builder(access = AccessLevel.PUBLIC)
    private Review(Integer score, String content, User user, Enrollment enrollment, Lecture lecture, Review parent) {
        this.score = score;
        this.content = content;
        this.user = user;
        this.enrollment = enrollment;
        this.lecture = lecture;
        this.parent = parent;
    }

    public void addChild(Review review) {
        this.children.add(review);
        review.setParent(this);
    }

    public void delete() {
        // TODO - CHECK : mappedBy된 리스트 값
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        this.children.clear();
    }

    public void updateMentorReview(MentorReviewUpdateRequest mentorReviewUpdateRequest) {
        setContent(mentorReviewUpdateRequest.getContent());
    }

    public void updateMenteeReview(MenteeReviewUpdateRequest menteeReviewUpdateRequest) {
        setScore(menteeReviewUpdateRequest.getScore());
        setContent(menteeReviewUpdateRequest.getContent());
    }

    // buildParentReview
    public static Review buildMenteeReview(User user, Lecture lecture, Enrollment enrollment, MenteeReviewCreateRequest menteeReviewCreateRequest) {
        return menteeReviewCreateRequest.toEntity(user, lecture, enrollment);
    }

    // buildChildReview
    public static Review buildMentorReview(User user, Lecture lecture, Review parent, MentorReviewCreateRequest mentorReviewCreateRequest) {
        Review review = mentorReviewCreateRequest.toEntity(user, lecture, parent);
        // TODO - CHECK : id check
        parent.addChild(review);
        return review;
    }
}
