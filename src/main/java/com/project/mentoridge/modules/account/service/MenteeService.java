package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.log.component.MenteeLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MenteeService extends AbstractService {

    private final MenteeRepository menteeRepository;
    private final MenteeLogService menteeLogService;

    private final ChatroomRepository chatroomRepository;
    private final MessageRepository messageRepository;

    private final PickRepository pickRepository;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;

        private Page<Mentee> getMentees(Integer page) {
            return menteeRepository.findAll(getPageRequest(page));
        }

    @Transactional(readOnly = true)
    public Page<MenteeResponse> getMenteeResponses(Integer page) {
        return getMentees(page).map(MenteeResponse::new);
    }

    @Transactional(readOnly = true)
    public MenteeResponse getMenteeResponse(Long menteeId) {
        return new MenteeResponse(getMentee(menteeRepository, menteeId));
    }

    public void updateMentee(User menteeUser, MenteeUpdateRequest menteeUpdateRequest) {

        Mentee mentee = getMentee(menteeRepository, menteeUser);
        mentee.update(menteeUpdateRequest, menteeUser, menteeLogService);
    }

    public void deleteMentee(User menteeUser) {

        Mentee mentee = getMentee(menteeRepository, menteeUser);

        List<Long> chatroomIds = chatroomRepository.findIdsByMentee(mentee);
        messageRepository.deleteByChatroomIds(chatroomIds);
        // chatroom 삭제
        //chatroomRepository.deleteByMentee(mentee);
        chatroomRepository.deleteByIds(chatroomIds);
        // pick 삭제
        pickRepository.deleteByMentee(mentee);
        // enrollment 삭제
        enrollmentRepository.findByMentee(mentee).forEach(enrollment -> {
            enrollmentService.deleteEnrollment(enrollment);
        });
        mentee.delete(menteeUser, menteeLogService);
        menteeRepository.delete(mentee);
    }
}
