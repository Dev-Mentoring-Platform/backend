package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.account.vo.Mentor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LectureMentorResponse {

    private Long mentorId;
    // 총 강의 수
    private Long lectureCount = null;
    // 리뷰 개수
    private Long reviewCount = null;
    // 닉네임
    private String nickname;
    // 프로필사진
    private String image;

    public LectureMentorResponse(Mentor mentor) {
        this.mentorId = mentor.getId();
        this.nickname = mentor.getUser().getNickname();
        this.image = mentor.getUser().getImage();
    }
}
