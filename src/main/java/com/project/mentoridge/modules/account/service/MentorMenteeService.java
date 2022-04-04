package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.response.MenteeEnrollmentInfoResponse;
import com.project.mentoridge.modules.account.controller.response.MenteeSimpleResponse;
import com.project.mentoridge.modules.account.repository.MentorQueryRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.modules.account.enums.RoleType.MENTOR;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MentorMenteeService extends AbstractService {

    private final MentorRepository mentorRepository;
    private final MentorQueryRepository mentorQueryRepository;

        private Mentor getMentor(User user) {
            return Optional.ofNullable(mentorRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTOR));
        }

    public Page<MenteeSimpleResponse> getMenteeSimpleResponses(User user, Boolean closed, Integer page) {
        Mentor mentor = getMentor(user);
        return mentorQueryRepository.findMenteesOfMentor(mentor, closed, getPageRequest(page));
    }

    public Page<MenteeEnrollmentInfoResponse> getMenteeLectureResponses(User user, Boolean closed, Long menteeId, Integer page) {
        Mentor mentor = getMentor(user);
        return mentorQueryRepository.findMenteeLecturesOfMentor(mentor, closed, menteeId, getPageRequest(page));
    }
}
