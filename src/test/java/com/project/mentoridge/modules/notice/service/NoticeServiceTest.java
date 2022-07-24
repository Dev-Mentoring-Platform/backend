package com.project.mentoridge.modules.notice.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.notice.controller.response.NoticeResponse;
import com.project.mentoridge.modules.notice.repository.NoticeRepository;
import com.project.mentoridge.modules.notice.vo.Notice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {

    @InjectMocks
    NoticeService noticeService;
    @Mock
    NoticeRepository noticeRepository;

    @Test
    void getNoticeResponses() {

        // given
        when(noticeRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(mock(Notice.class), mock(Notice.class), mock(Notice.class))));
        // when
        Page<NoticeResponse> response = noticeService.getNoticeResponses(1);
        // then
        assertThat(response.getContent()).hasSize(3);
    }

    @Test
    void getNoticeResponse_when_not_exist() {

        // given
        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> noticeService.getNoticeResponse(1L));
    }

    @Test
    void getNoticeResponse() {

        // given
        Notice notice = Notice.builder()
                .title("title")
                .content("content")
                .build();
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));
        // when
        NoticeResponse response = noticeService.getNoticeResponse(1L);
        // then
        assertThat(response.getTitle()).isEqualTo(notice.getTitle());
        assertThat(response.getContent()).isEqualTo(notice.getContent());
    }
}
