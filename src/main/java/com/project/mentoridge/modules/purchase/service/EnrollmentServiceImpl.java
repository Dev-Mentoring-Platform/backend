package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.firebase.service.AndroidPushNotificationsService;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.service.NotificationService;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.repository.CancellationRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentQueryRepository;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.config.exception.AlreadyExistException.ENROLLMENT;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE_PRICE;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;

@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentServiceImpl extends AbstractService implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentQueryRepository enrollmentQueryRepository;
    private final CancellationRepository cancellationRepository;
    private final MenteeRepository menteeRepository;

    private final LectureRepository lectureRepository;
    private final LecturePriceRepository lecturePriceRepository;

    private final ChatroomRepository chatroomRepository;
    private final ChatService chatService;
    private final ReviewRepository reviewRepository;

    private final AndroidPushNotificationsService androidPushNotificationsService;
    private final NotificationService notificationService;

    private Page<Lecture> getLecturesOfMentee(User user, Integer page) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));

        return enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee, PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").ascending()))
                .map(Enrollment::getLecture);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<LectureResponse> getLectureResponsesOfMentee(User user, Integer page) {
        return getLecturesOfMentee(user, page).map(LectureResponse::new);
    }

    // getUnreviewedLecturesOfMentee
    @Transactional(readOnly = true)
    @Override
    public Page<EnrollmentWithSimpleLectureResponse> getEnrollmentWithSimpleLectureResponses(User user, boolean reviewed, Integer page) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));

        return enrollmentQueryRepository.findEnrollments(mentee, reviewed, Pageable.ofSize(page));
    }

    @Override
    public Enrollment createEnrollment(User user, Long lectureId, Long lecturePriceId) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));

        // TODO - CHECK : lecture & mentor - fetch join
        // TODO - CHECK : lecture의 enrollment가 null vs mentee의 enrollment는 size = 0
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        LecturePrice lecturePrice = lecturePriceRepository.findByLectureAndId(lecture, lecturePriceId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE_PRICE));

        // 종료/취소된 강의 재구매 불가
        if (enrollmentRepository.findAllByMenteeIdAndLectureId(mentee.getId(), lecture.getId()).isPresent()) {
            throw new AlreadyExistException(ENROLLMENT);
        }

        // TODO - 구매 프로세스
        // TODO - 구매 중복 X 체크 (UNIQUE)

        // 성공 시
        // TODO - CHECK
        Enrollment enrollment = enrollmentRepository.save(Enrollment.buildEnrollment(mentee, lecture, lecturePrice));

        Mentor mentor = lecture.getMentor();
        User mentorUser = mentor.getUser();
        // 수강 시 채팅방 자동 생성
        chatService.createChatroom(mentor, mentee, enrollment);
        // 강의 등록 시 멘토에게 알림 전송
        notificationService.createNotification(mentorUser, NotificationType.ENROLLMENT);
        // androidPushNotificationsService.send(mentorUser.getFcmToken(), "강의 등록", String.format("%s님이 %s 강의를 등록했습니다", user.getNickname(), lecture.getTitle()));
        return enrollment;
    }

//    @Override
//    public void close(User user, Long lectureId, Long enrollmentId) {
//
//        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
//                .orElseThrow(() -> new UnauthorizedException(RoleType.MENTOR));
//
//        Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
//                .orElseThrow(() -> new EntityNotFoundException(LECTURE));
//
//        Enrollment enrollment = enrollmentRepository.findByLectureAndId(lecture, enrollmentId)
//                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.ENROLLMENT));
//
//        enrollment.close();
//        // 수강 종료 시 채팅방 삭제
//        // enrollment.setChatroom(null);
//        chatService.deleteChatroom(enrollment);
//    }
    @Override
    public void close(User user, Long lectureId) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        Enrollment enrollment = enrollmentRepository.findByMenteeAndLectureAndCanceledFalseAndClosedFalse(mentee, lecture)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.ENROLLMENT));

        enrollment.close();
        // 수강 종료 시 채팅방 삭제
        // enrollment.setChatroom(null);
        chatService.deleteChatroom(enrollment);
    }

    @Override
    public void deleteEnrollment(Enrollment enrollment) {

        // TODO - Optional 사용법
        // 이미 취소/종료된 강의인 경우
        // chatService.deleteChatroom(enrollment);
        // chatroomRepository.deleteByEnrollment(enrollment);
        chatroomRepository.findByEnrollment(enrollment).ifPresent(
                chatroom -> chatroomRepository.delete(chatroom)
        );

        Optional.ofNullable(reviewRepository.findByEnrollment(enrollment)).ifPresent(
                review -> {
                    review.delete();
                    reviewRepository.delete(review);
                }
        );

        cancellationRepository.deleteByEnrollment(enrollment);

        enrollment.delete();
        enrollmentRepository.deleteEnrollmentById(enrollment.getId());
    }
}
