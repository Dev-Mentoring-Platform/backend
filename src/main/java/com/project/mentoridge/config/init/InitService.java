package com.project.mentoridge.config.init;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class InitService {

//    private final LoginService loginService;
//    private final MentorService mentorService;
//    private final LectureService lectureService;
//    private final EnrollmentService enrollmentService;
//    private final CancellationService cancellationService;
//    private final ReviewService reviewService;
//    private final MentorCancellationService mentorCancellationService;
//
//    private final UserRepository userRepository;
//    private final MenteeRepository menteeRepository;
//    private final CareerRepository careerRepository;
//    private final EducationRepository educationRepository;
//    private final MentorRepository mentorRepository;
//    private final LectureRepository lectureRepository;
//    private final LecturePriceRepository lecturePriceRepository;
//    private final LectureSubjectRepository lectureSubjectRepository;
//
//    private final CancellationRepository cancellationRepository;
//    private final EnrollmentRepository enrollmentRepository;
//    private final ChatroomRepository chatroomRepository;
//    private final ReviewRepository reviewRepository;
//
//    private final NotificationRepository notificationRepository;
//    private final SubjectRepository subjectRepository;

    // @PostConstruct
    @Transactional
    void init() {

    }

}
