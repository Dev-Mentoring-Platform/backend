package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.chat.service.ChatroomService;
import com.project.mentoridge.modules.purchase.controller.response.CancellationResponse;
import com.project.mentoridge.modules.purchase.repository.CancellationQueryRepository;
import com.project.mentoridge.modules.purchase.repository.CancellationRepository;
import com.project.mentoridge.modules.purchase.vo.Cancellation;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.CANCELLATION;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MentorCancellationService extends AbstractService {

    private final CancellationRepository cancellationRepository;
    private final CancellationQueryRepository cancellationQueryRepository;
    private final MentorRepository mentorRepository;

    public Page<CancellationResponse> getCancellationResponses(User user, Integer page) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        return cancellationQueryRepository.findCancellationsOfMentor(mentor, getPageRequest(page));
    }

    @Transactional
    public void approve(User user, Long cancellationId) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));

        Cancellation cancellation = cancellationRepository.findById(cancellationId)
                .orElseThrow(() -> new EntityNotFoundException(CANCELLATION));
        cancellation.approve();

        Enrollment enrollment = cancellation.getEnrollment();
        enrollment.cancel();

        // TODO - 환불
    }

}
