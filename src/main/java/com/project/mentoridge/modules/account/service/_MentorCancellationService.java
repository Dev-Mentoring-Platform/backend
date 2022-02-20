package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.modules.base.AbstractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class _MentorCancellationService extends AbstractService {
/*
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
    }*/

}
