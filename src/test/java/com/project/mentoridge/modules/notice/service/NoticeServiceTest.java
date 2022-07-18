package com.project.mentoridge.modules.notice.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.notice.repository.NoticeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
        // when
        noticeService.getNoticeResponses(1);
        // then
        verify(noticeRepository).findAll(any(Pageable.class));
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
        // when
        noticeService.getNoticeResponse(1L);
        // then
        verify(noticeRepository).findById(1L);
    }
}
