package com.project.mentoridge.modules.purchase.controller.response;

import com.project.mentoridge.modules.lecture.controller.response.SimpleLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Pick;
import lombok.Data;

@Data
public class PickWithSimpleLectureResponse {

    private Long pickId;
    private SimpleLectureResponse lecture;

    public PickWithSimpleLectureResponse(Pick pick) {
        this.pickId = pick.getId();
        this.lecture = new SimpleLectureResponse(pick.getLecture(), pick.getLecturePrice());
    }

}
