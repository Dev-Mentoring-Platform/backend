package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.controller.response.PickWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.vo.Pick;
import org.springframework.data.domain.Page;

public interface PickService {

//    Page<Pick> getPicks(User user, Integer page);
//    Page<PickResponse> getPickResponses(User user, Integer page);
    Page<PickWithSimpleLectureResponse> getPickWithSimpleLectureResponses(User user, Integer page);

    Pick createPick(User user, Long lectureId, Long lecturePriceId);
    void deletePick(User user, Long pickId);
    void deleteAllPicks(User user);

}
