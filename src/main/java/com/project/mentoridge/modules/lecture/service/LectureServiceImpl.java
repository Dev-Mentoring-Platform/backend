package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureMentorResponse;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureQueryRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSearchRepository;
import com.project.mentoridge.modules.lecture.repository.dto.LectureMentorQueryDto;
import com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.log.component.LecturePriceLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LectureServiceImpl extends AbstractService implements LectureService {

    private final LectureRepository lectureRepository;
    private final LecturePriceRepository lecturePriceRepository;
    private final LectureSearchRepository lectureSearchRepository;
    private final LectureQueryRepository lectureQueryRepository;
    private final LectureLogService lectureLogService;
    private final LecturePriceLogService lecturePriceLogService;

    private final UserRepository userRepository;
    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;
    private final PickRepository pickRepository;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    private final MenteeReviewRepository menteeReviewRepository;
    // private final MentorReviewRepository mentorReviewRepository;
    private final SubjectRepository subjectRepository;

        private User getUser(String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException(USER));
        }

        private Lecture getLecture(Long lectureId) {
            return lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        }

        private Lecture getLecture(Mentor mentor, Long lectureId) {
            return lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        }

    @Override
    public LectureResponse getLectureResponse(User user, Long lectureId) {

        Lecture lecture = getLecture(lectureId);
        LectureResponse lectureResponse = new LectureResponse(lecture);
        List<LecturePriceResponse> lecturePrices = lecturePriceRepository.findByLecture(lecture).stream()
                .map(LecturePriceResponse::new).collect(Collectors.toList());
        lectureResponse.setLecturePrices(lecturePrices);
        // TODO - 쿼리
        setLectureReview(lectureResponse);
        setLectureMentor(lectureResponse);
        // 로그인한 경우 - 좋아요 여부 표시
        // setPicked(user, lectureId, lectureResponse);

        return lectureResponse;
    }

    @Override
    public EachLectureResponse getEachLectureResponse(User user, Long lectureId, Long lecturePriceId) {

        LecturePrice lecturePrice = lecturePriceRepository.findByLectureIdAndLecturePriceId(lectureId, lecturePriceId);
        EachLectureResponse response = new EachLectureResponse(lecturePrice, lecturePrice.getLecture());
/*
        lectureQueryRepository.findLectureReviewQueryDto(lectureId, lecturePriceId).ifPresent(lectureReviewQueryDto -> {
            response.setReviewCount(lectureReviewQueryDto.getReviewCount());
            response.setScoreAverage(lectureReviewQueryDto.getScoreAverage());
        });*/
        Optional<LectureReviewQueryDto> optional = lectureQueryRepository.findLectureReviewQueryDto(lectureId, lecturePriceId);
        if (optional.isPresent()) {
            LectureReviewQueryDto lectureReviewQueryDto = optional.get();
            response.setReviewCount(lectureReviewQueryDto.getReviewCount());
            response.setScoreAverage(lectureReviewQueryDto.getScoreAverage());
        } else {
            response.setReviewCount(0L);
            response.setScoreAverage(0.0);
        }
        setLectureMentor(response);
        setPicked(user, lectureId, lecturePriceId, response);

        return response;
    }

    // TODO - CHECK : mapstruct vs 생성자
    // return lectureMapstructUtil.getLectureResponse(getLecture(lectureId));
    @Override
    public Page<EachLectureResponse> getEachLectureResponses(User user, String zone, LectureListRequest lectureListRequest, Integer page) {

        // 2022.04.03 - 강의 가격별로 리스트 출력
        Page<EachLectureResponse> lecturePrices = lectureSearchRepository.findLecturePricesByZoneAndSearch(
                user, AddressUtils.convertStringToEmbeddableAddress(zone), lectureListRequest, getPageRequest(page))
                .map(lecturePrice -> new EachLectureResponse(lecturePrice, lecturePrice.getLecture()));

        // 컬렉션 조회 최적화
        // - 컬렉션을 MAP 한방에 조회
        List<Long> lectureIds = lecturePrices.stream().map(EachLectureResponse::getLectureId).collect(Collectors.toList());
        List<Long> lecturePriceIds = lecturePrices.stream().map(lecturePrice -> lecturePrice.getLecturePrice().getLecturePriceId()).collect(Collectors.toList());

        // 2022.04.18 - lecturePriceId 기준으로 enrollmentCount
        Map<Long, Long> lectureEnrollmentQueryDtoMap = lectureQueryRepository.findLectureEnrollmentQueryDtoMap(lecturePriceIds);
        // lecturePriceId 기준
        Map<Long, Long> lecturePickQueryDtoMap = lectureQueryRepository.findLecturePickQueryDtoMap(lecturePriceIds);

        // lectureId, lecturePriceId 기준
        Map<Long, LectureReviewQueryDto> lectureReviewQueryDtoMap = lectureQueryRepository.findLectureReviewQueryDtoMap(lectureIds, lecturePriceIds);
        // lectureId 기준
        Map<Long, LectureMentorQueryDto> lectureMentorQueryDtoMap = lectureQueryRepository.findLectureMentorQueryDtoMap(lectureIds);
        // picked 여부
        Map<Long, Boolean> picked = getEachLecturePickedByUser(user, lecturePriceIds);

        lecturePrices.forEach(eachLectureResponse -> {

            Long lectureId = eachLectureResponse.getLectureId();
            Long lecturePriceId = eachLectureResponse.getLecturePrice().getLecturePriceId();

            if (lectureEnrollmentQueryDtoMap.size() != 0 && lectureEnrollmentQueryDtoMap.get(lecturePriceId) != null) {
                eachLectureResponse.setEnrollmentCount(lectureEnrollmentQueryDtoMap.get(lecturePriceId));
            } else {
                eachLectureResponse.setEnrollmentCount(0L);
            }

            if (lecturePickQueryDtoMap.size() != 0 && lecturePickQueryDtoMap.get(lecturePriceId) != null) {
                eachLectureResponse.setPickCount(lecturePickQueryDtoMap.get(lecturePriceId));
            } else {
                eachLectureResponse.setPickCount(0L);
            }

            LectureReviewQueryDto lectureReviewQueryDto = null;
            if (lectureReviewQueryDtoMap.size() != 0 && lectureReviewQueryDtoMap.get(lecturePriceId) != null) {
                lectureReviewQueryDto = lectureReviewQueryDtoMap.get(lecturePriceId);
            }
            if (lectureReviewQueryDto != null) {
                eachLectureResponse.setReviewCount(lectureReviewQueryDto.getReviewCount());
                eachLectureResponse.setScoreAverage(lectureReviewQueryDto.getScoreAverage());
            } else {
                eachLectureResponse.setReviewCount(0L);
                eachLectureResponse.setScoreAverage(0.0);
            }

            LectureMentorResponse lectureMentorResponse = eachLectureResponse.getLectureMentor();
            LectureMentorQueryDto lectureMentorQueryDto = lectureMentorQueryDtoMap.get(lectureMentorResponse.getMentorId());
            if (lectureMentorQueryDto != null) {
                lectureMentorResponse.setLectureCount(lectureMentorQueryDto.getLectureCount());
                lectureMentorResponse.setReviewCount(lectureMentorQueryDto.getReviewCount());
            } else {
                lectureMentorResponse.setLectureCount(0L);
                lectureMentorResponse.setReviewCount(0L);
            }

            // 로그인한 경우 - 좋아요 여부 표시
            // setPicked(user, lectureId, lecturePriceId, eachLectureResponse);
            if (picked.get(lecturePriceId) != null) {
                eachLectureResponse.setPicked(true);
            } else {
                eachLectureResponse.setPicked(false);
            }
        });

        return lecturePrices;
    }

        private void setLectureReview(LectureResponse lectureResponse) {

            Lecture lecture = getLecture(lectureResponse.getLectureId());

            List<MenteeReview> reviews = menteeReviewRepository.findByLecture(lecture);
            lectureResponse.setReviewCount((long) reviews.size());
            OptionalDouble scoreAverage = reviews.stream().map(MenteeReview::getScore).mapToInt(Integer::intValue).average();
            lectureResponse.setScoreAverage(scoreAverage.isPresent() ? scoreAverage.getAsDouble() : 0.0);

        }

        private void setLectureMentor(LectureResponse lectureResponse) {

            Mentor mentor = getLecture(lectureResponse.getLectureId()).getMentor();
            List<Lecture> lectures = lectureRepository.findByMentor(mentor);

            LectureMentorResponse lectureMentorResponse = lectureResponse.getLectureMentor();
            lectureMentorResponse.setLectureCount((long) lectures.size());
            lectureMentorResponse.setReviewCount((long) menteeReviewRepository.countByLectureIn(lectures));
            lectureResponse.setLectureMentor(lectureMentorResponse);
        }

        // TODO - CHECK
        // pick - lecturePriceId, true/false
        private Map<Long, Boolean> getEachLecturePickedByUser(User user, List<Long> lecturePriceIds) {

            Mentee mentee = menteeRepository.findByUser(user);
            List<Long> pickedLecturePriceIds = pickRepository.findByMenteeAndLecturePriceIds(mentee, lecturePriceIds)
                    .stream().map(pick -> pick.getLecturePrice().getId()).collect(Collectors.toList());

            Map<Long, Boolean> map = new HashMap<>();
            for (Long pickedLecturePriceId : pickedLecturePriceIds) {
                map.put(pickedLecturePriceId, true);
            }
            return map;
        }

        private void setPicked(User user, Long lectureId, Long lecturePriceId, EachLectureResponse eachLectureResponse) {

            if (user == null) {
                return;
            }
            Optional.ofNullable(menteeRepository.findByUser(user)).ifPresent(mentee -> {
                Optional<Pick> optional = pickRepository.findByMenteeAndLectureIdAndLecturePriceId(mentee, lectureId, lecturePriceId);
                if (optional.isPresent()) {
                    eachLectureResponse.setPicked(true);
                } else {
                    eachLectureResponse.setPicked(false);
                }
            });
        }

        private void setLectureMentor(EachLectureResponse eachLectureResponse) {

            Mentor mentor = getLecture(eachLectureResponse.getLectureId()).getMentor();
            List<Lecture> lectures = lectureRepository.findByMentor(mentor);

            LectureMentorResponse lectureMentorResponse = eachLectureResponse.getLectureMentor();
            lectureMentorResponse.setLectureCount((long) lectures.size());
            lectureMentorResponse.setReviewCount((long) menteeReviewRepository.countByLectureIn(lectures));
            eachLectureResponse.setLectureMentor(lectureMentorResponse);
        }

    @Transactional
    @Override
    public Lecture createLecture(User user, LectureCreateRequest lectureCreateRequest) {

        Mentor mentor = getMentor(mentorRepository, user);

        Lecture lecture = lectureCreateRequest.toEntity(mentor);
        for (LectureCreateRequest.LecturePriceCreateRequest lecturePriceCreateRequest : lectureCreateRequest.getLecturePrices()) {
            lecture.addPrice(lecturePriceCreateRequest.toEntity(null));
        }
        for (LectureCreateRequest.LectureSubjectCreateRequest lectureSubjectCreateRequest : lectureCreateRequest.getLectureSubjects()) {
            Subject subject = subjectRepository.findById(lectureSubjectCreateRequest.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException(SUBJECT));
            LectureSubject lectureSubject = LectureSubject.builder()
                    .lecture(null)
                    .subject(subject)
                    .build();
            lecture.addSubject(lectureSubject);
        }
        Lecture saved = lectureRepository.save(lecture);
        lectureLogService.insert(user, saved);
        return saved;
    }

    @Transactional
    @Override
    public void updateLecture(User user, Long lectureId, LectureUpdateRequest lectureUpdateRequest) {

        Mentor mentor = getMentor(mentorRepository, user);
        Lecture lecture = getLecture(mentor, lectureId);

        // 등록된 적 있는 강의면 수정 불가
        if (enrollmentRepository.countByLecture(lecture) > 0) {
            // TODO - 예외 처리
            throw new RuntimeException("등록된 강의는 수정이 불가합니다.");
        }
        lecture.update(lectureUpdateRequest, subjectRepository, user, lectureLogService);
    }

    // TODO - CHECK : 리팩토링
    // TODO - CHECK : 로그
    @Transactional
    @Override
    public void deleteLecture(Lecture lecture) {

        // mentor_review
        // mentee_review
        // enrollment
        enrollmentRepository.findByLecture(lecture).forEach(enrollmentService::deleteEnrollment);
        // pick
        pickRepository.deleteByLecture(lecture);
        // lecture_price
        // lecture_subject
        // lecture_system_type
        lectureRepository.delete(lecture);
    }

    @Transactional
    @Override
    public void deleteLecture(User user, Long lectureId) {

        Mentor mentor = getMentor(mentorRepository, user);

        Lecture lecture = getLecture(mentor, lectureId);
        lecture.delete(user, lectureLogService);
        deleteLecture(lecture);
    }

    @Transactional
    @Override
    public void approve(User user, Long lectureId) {
        // user = getUser(user.getUsername());
        if (user.getRole() != RoleType.ADMIN) {
            throw new UnauthorizedException(RoleType.ADMIN);
        }
        Lecture lecture = getLecture(lectureId);
        lecture.approve(lectureLogService);
    }

    @Transactional
    @Override
    public void open(User user, Long lectureId, Long lecturePriceId) {

        user = getUser(user.getUsername());
        Lecture lecture = getLecture(lectureId);
        // TODO - CHECK
//        if (!lecture.getMentor().getUser().equals(user)) {
//            throw new UnauthorizedException();
//        }

        LecturePrice lecturePrice = lecturePriceRepository.findByLectureAndId(lecture, lecturePriceId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE_PRICE));
        lecturePrice.open(user, lecturePriceLogService);
    }

    @Transactional
    @Override
    public void close(User user, Long lectureId, Long lecturePriceId) {

        user = getUser(user.getUsername());
        Lecture lecture = getLecture(lectureId);
        // TODO - CHECK
//        if (!lecture.getMentor().getUser().equals(user)) {
//            throw new UnauthorizedException();
//        }

        LecturePrice lecturePrice = lecturePriceRepository.findByLectureAndId(lecture, lecturePriceId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE_PRICE));
        lecturePrice.close(user, lecturePriceLogService);
    }
}
