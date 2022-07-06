package com.project.mentoridge.modules.purchase.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.SimpleEachLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Pick;
import lombok.Data;

@Data
public class PickWithSimpleEachLectureResponse {

    private Long pickId;
    private SimpleEachLectureResponse lecture;

    public PickWithSimpleEachLectureResponse(Pick pick) {
        this.pickId = pick.getId();
        this.lecture = new SimpleEachLectureResponse(pick.getLecture(), pick.getLecturePrice());
    }

}
