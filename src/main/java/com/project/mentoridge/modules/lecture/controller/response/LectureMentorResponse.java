package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.account.vo.Mentor;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class LectureMentorResponse {

    private Long mentorId;
    // 닉네임
    private String nickname;
    // 프로필사진
    private String image;

    // 총 강의 수
    private Long lectureCount = null;
    // 리뷰 개수
    private Long reviewCount = null;

    public LectureMentorResponse(Mentor mentor) {
        this.mentorId = mentor.getId();
        this.nickname = mentor.getUser().getNickname();
        this.image = mentor.getUser().getImage();
//        this.lectureCount = null;
//        this.reviewCount = null;
    }
}
