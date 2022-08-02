package com.project.mentoridge.modules.notice.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import com.project.mentoridge.modules.notice.controller.response.NoticeResponse;
import com.project.mentoridge.modules.notice.repository.NoticeRepository;
import com.project.mentoridge.modules.notice.vo.Notice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class NoticeServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    NoticeService noticeService;
    @Autowired
    NoticeRepository noticeRepository;

    private Notice notice1;
    private Notice notice2;

    @BeforeEach
    @Override
    protected void init() {

        initDatabase();
        notice1 = noticeRepository.save(Notice.builder()
                .title("title1")
                .content("content1")
                .build());
        notice2 = noticeRepository.save(Notice.builder()
                .title("title2")
                .content("content2")
                .build());
    }

    @Test
    void get_paged_NoticeResponses() {

        // given
        // when
        Page<NoticeResponse> noticeResponses = noticeService.getNoticeResponses(1);
        // then
        assertThat(noticeResponses.getTotalElements()).isEqualTo(2L);
        for(NoticeResponse noticeResponse : noticeResponses) {

            if (Objects.equals(noticeResponse.getNoticeId(), notice1.getId())) {
                assertAll(
                        () -> assertThat(noticeResponse.getNoticeId()).isEqualTo(notice1.getId()),
                        () -> assertThat(noticeResponse.getTitle()).isEqualTo(notice1.getTitle()),
                        () -> assertThat(noticeResponse.getContent()).isEqualTo(notice1.getContent()),
                        () -> assertThat(noticeResponse.getCreatedAt()).isNotNull()
                );
            } else if (Objects.equals(noticeResponse.getNoticeId(), notice2.getId())) {
                assertAll(
                        () -> assertThat(noticeResponse.getNoticeId()).isEqualTo(notice2.getId()),
                        () -> assertThat(noticeResponse.getTitle()).isEqualTo(notice2.getTitle()),
                        () -> assertThat(noticeResponse.getContent()).isEqualTo(notice2.getContent()),
                        () -> assertThat(noticeResponse.getCreatedAt()).isNotNull()
                );
            }
        }
    }

    @Test
    void get_NoticeResponse() {

        // given
        // when
        NoticeResponse noticeResponse = noticeService.getNoticeResponse(notice1.getId());
        // then
        assertAll(
                () -> assertThat(noticeResponse.getNoticeId()).isEqualTo(notice1.getId()),
                () -> assertThat(noticeResponse.getTitle()).isEqualTo(notice1.getTitle()),
                () -> assertThat(noticeResponse.getContent()).isEqualTo(notice1.getContent()),
                () -> assertThat(noticeResponse.getCreatedAt()).isNotNull()
        );
    }

}