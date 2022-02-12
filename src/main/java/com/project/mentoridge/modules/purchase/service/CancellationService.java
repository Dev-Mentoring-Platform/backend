package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.controller.request.CancellationCreateRequest;
import com.project.mentoridge.modules.purchase.vo.Cancellation;

public interface CancellationService {

    // 수강 취소 요청
    Cancellation cancel(User user, Long lectureId, CancellationCreateRequest cancellationCreateRequest);
}
