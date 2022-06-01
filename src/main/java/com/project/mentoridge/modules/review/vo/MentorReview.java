package com.project.mentoridge.modules.review.vo;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.log.component.MentorReviewLogService;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import lombok.*;

import javax.persistence.*;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "mentor_review_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter(AccessLevel.PRIVATE)
@Entity
public class MentorReview extends BaseEntity {

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id",
            referencedColumnName = "mentor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENTOR_REVIEW_MENTOR_ID"))
    private Mentor mentor;

    // TODO - CHECK : @OneToOne
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id",
            referencedColumnName = "mentee_review_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MENTOR_REVIEW_PARENT_ID"))
    private MenteeReview parent;

    @Builder(access = AccessLevel.PUBLIC)
    private MentorReview(String content, Mentor mentor, MenteeReview parent) {
        this.content = content;
        this.mentor = mentor;
        this.parent = parent;
    }

    public void delete() {
        this.parent.getChildren().remove(this);
    }

    public void delete(User user, MentorReviewLogService mentorReviewLogService) {
        this.delete();
        mentorReviewLogService.delete(user, this);
    }

    public void setParent(MenteeReview parent) {
        this.parent = parent;
    }

    public void update(MentorReviewUpdateRequest mentorReviewUpdateRequest, User user, MentorReviewLogService mentorReviewLogService) {
        MentorReview before = this.copy();
        setContent(mentorReviewUpdateRequest.getContent());
        mentorReviewLogService.update(user, before, this);
    }

    private MentorReview copy() {
        return MentorReview.builder()
                .content(content)
                .mentor(mentor)
                .parent(parent)
                .build();
    }
}
