package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class _CancellationServiceImpl extends AbstractService {

//    private final EnrollmentRepository enrollmentRepository;
//    private final CancellationRepository cancellationRepository;
//    private final MenteeRepository menteeRepository;
//
//    private final LectureRepository lectureRepository;
//
//    @Override
//    public Cancellation cancel(User user, Long lectureId, CancellationCreateRequest cancellationCreateRequest) {
//
//        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
//                .orElseThrow(() -> new UnauthorizedException(MENTEE));
//
//        Lecture lecture = lectureRepository.findById(lectureId)
//                .orElseThrow(() -> new EntityNotFoundException(LECTURE));
//
//        Enrollment enrollment = enrollmentRepository.findByMenteeAndLectureAndCanceledFalseAndClosedFalse(mentee, lecture)
//                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.ENROLLMENT));
//
//        enrollment.cancel();
//
//        // TODO - Entity Listener 활용해 변경
//        return cancellationRepository.save(cancellationCreateRequest.toEntity(enrollment));
//    }

}
