package com.project.mentoridge.modules.notice.controller;

import com.project.mentoridge.modules.notice.controller.response.NoticeResponse;
import com.project.mentoridge.modules.notice.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"NoticeController"})
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@RestController
public class NoticeController {

    private final NoticeService noticeService;

    @ApiOperation("공지사항 리스트 - 페이징")
    @GetMapping
    public ResponseEntity<?> getNotices(@RequestParam(defaultValue = "1") Integer page) {
        Page<NoticeResponse> notices = noticeService.getNoticeResponses(page);
        return ResponseEntity.ok(notices);
    }

    @ApiOperation("공지사항 조회")
    @GetMapping("/{notice_id}")
    public ResponseEntity<?> getNotice(@PathVariable(name = "notice_id") Long noticeId) {
        NoticeResponse notice = noticeService.getNoticeResponse(noticeId);
        return ResponseEntity.ok(notice);
    }
}
