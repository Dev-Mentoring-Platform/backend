package com.project.mentoridge.modules.notice.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.notice.controller.response.NoticeResponse;
import com.project.mentoridge.modules.notice.repository.NoticeRepository;
import com.project.mentoridge.modules.notice.vo.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.NOTICE;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NoticeService extends AbstractService {

    private final NoticeRepository noticeRepository;

    // 관리자
    // create
    // update
    // delete

    public Page<NoticeResponse> getNoticeResponses(Integer page) {
        return noticeRepository.findAll(getPageRequest(page)).map(NoticeResponse::new);
    }

    public NoticeResponse getNoticeResponse(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new EntityNotFoundException(NOTICE));
        return new NoticeResponse(notice);
    }


}
