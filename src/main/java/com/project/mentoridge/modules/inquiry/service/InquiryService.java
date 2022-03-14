package com.project.mentoridge.modules.inquiry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import com.project.mentoridge.modules.log.component.InquiryLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.USER;

@Transactional
@RequiredArgsConstructor
@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final InquiryLogService inquiryLogService;

//    private final Producer producer;
    private final ObjectMapper objectMapper;

    static final String ROUTING_KEY = "CREATE_INQUIRY_QUEUE";

    public Inquiry createInquiry(User user, InquiryCreateRequest inquiryCreateRequest) {

        user = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(USER));

        Inquiry saved = inquiryRepository.save(inquiryCreateRequest.toEntity(user));
        inquiryLogService.insert(user, saved);
        return saved;
    }
/*
    public Inquiry test(InquiryCreateRequest inquiryCreateRequest) throws JsonProcessingException {

        User user = userRepository.findAll().stream().findFirst()
                .orElseThrow(RuntimeException::new);

        Inquiry inquiry = inquiryCreateRequest.toEntity(user);
        String message = objectMapper.writeValueAsString(inquiry);
        producer.send(ROUTING_KEY, message);

        return inquiry;
    }*/
}
