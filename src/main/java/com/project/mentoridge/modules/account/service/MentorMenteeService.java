package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.account.controller.response.MenteeEnrollmentInfoResponse;
import com.project.mentoridge.modules.account.controller.response.SimpleMenteeResponse;
import com.project.mentoridge.modules.account.repository.MentorQueryRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MentorMenteeService extends AbstractService {

    private final MentorRepository mentorRepository;
    private final MentorQueryRepository mentorQueryRepository;

    public List<SimpleMenteeResponse> getSimpleMenteeResponses(User mentorUser, Boolean closed, Boolean checked) {
        Mentor mentor = getMentor(mentorRepository, mentorUser);
        return mentorQueryRepository.findMenteesOfMentor(mentor, closed, checked);
    }

    public Page<MenteeEnrollmentInfoResponse> getMenteeLectureResponses(User mentorUser, Long menteeId, Integer page) {
        Mentor mentor = getMentor(mentorRepository, mentorUser);
        return mentorQueryRepository.findMenteeLecturesOfMentor(mentor, menteeId, getPageRequest(page));
    }

    public MenteeEnrollmentInfoResponse getMenteeLectureResponse(User mentorUser, Long menteeId, Long enrollmentId) {
        Mentor mentor = getMentor(mentorRepository, mentorUser);
        return mentorQueryRepository.findMenteeLectureOfMentor(mentor, menteeId, enrollmentId);
    }
}
