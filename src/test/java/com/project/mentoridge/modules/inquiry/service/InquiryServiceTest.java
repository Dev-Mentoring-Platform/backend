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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        User user = getUserWithName("user");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        // when
        InquiryCreateRequest inquiryCreateRequest = Mockito.mock(InquiryCreateRequest.class);
        inquiryService.createInquiry(user, inquiryCreateRequest);
        // then
        Inquiry inquiry = inquiryCreateRequest.toEntity(user);
        verify(inquiryRepository).save(inquiry);
        verify(inquiryLogService).insert(user, inquiry);
    }
}
