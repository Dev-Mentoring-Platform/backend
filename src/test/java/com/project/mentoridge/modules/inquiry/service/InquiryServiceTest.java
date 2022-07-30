package com.project.mentoridge.modules.inquiry.service;

import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import com.project.mentoridge.modules.log.component.InquiryLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InquiryServiceTest {

    @InjectMocks
    InquiryService inquiryService;

    @Mock
    InquiryRepository inquiryRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    InquiryLogService inquiryLogService;

    // org.mockito.exceptions.misusing.WrongTypeOfReturnValue
    // org.mockito.exceptions.misusing.UnfinishedStubbingException
    @Test
    void createInquiry() {
        // user, inquiryCreateRequest

        // given
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        InquiryCreateRequest inquiryCreateRequest = mock(InquiryCreateRequest.class);
        inquiryService.createInquiry(user, inquiryCreateRequest);

        // then
        Inquiry inquiry = mock(Inquiry.class);
        when(inquiryCreateRequest.toEntity(user)).thenReturn(inquiry);
        verify(inquiryRepository).save(inquiry);

        Inquiry saved = Inquiry.builder()
                .build();
        when(inquiryRepository.save(inquiry)).thenReturn(saved);
        verify(inquiryLogService).insert(user, saved);
    }
}
