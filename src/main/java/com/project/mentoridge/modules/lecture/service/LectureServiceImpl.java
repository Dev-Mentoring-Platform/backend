package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureListRequest;
import com.project.mentoridge.modules.lecture.controller.request.LectureUpdateRequest;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.repository.LectureQueryRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSearchRepository;
import com.project.mentoridge.modules.lecture.repository.dto.LectureMentorQueryDto;
import com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.review.repository.ReviewRepository;
import com.project.mentoridge.modules.review.vo.Review;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.*;
import static com.project.mentoridge.modules.account.enums.RoleType.ADMIN;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTOR;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LectureServiceImpl extends AbstractService implements LectureService {

    private final LectureRepository lectureRepository;
    private final LectureSearchRepository lectureSearchRepository;
    private final LectureQueryRepository lectureQueryRepository;

    private final UserRepository userRepository;
    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;
    private final PickRepository pickRepository;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final SubjectRepository subjectRepository;

    private Lecture getLecture(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));
    }

    @Override
    public LectureResponse getLectureResponse(User user, Long lectureId) {

        Lecture lecture = getLecture(lectureId);
        LectureResponse lectureResponse = new LectureResponse(lecture);
        // TODO - 쿼리
        setLectureReview(lectureResponse);
        setLectureMentor(lectureResponse);
        // 로그인한 경우 - 좋아요 여부 표시
        setPicked(user, lectureId, lectureResponse);

        return lectureResponse;
    }

    // TODO - CHECK : mapstruct vs 생성자
    // return lectureMapstructUtil.getLectureResponse(getLecture(lectureId));
    @Override
    public Page<LectureResponse> getLectureResponses(User user, String zone, LectureListRequest lectureListRequest, Integer page) {

        // System.out.println(lectureListRequest);
        Page<LectureResponse> lectures = lectureSearchRepository.findLecturesByZoneAndSearch(
                AddressUtils.convertStringToEmbeddableAddress(zone), lectureListRequest, PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").ascending()))
                .map(LectureResponse::new);

        // 컬렉션 조회 최적화
        // - 컬렉션을 MAP 한방에 조회
        List<Long> lectureIds = lectures.stream().map(LectureResponse::getId).collect(Collectors.toList());
        Map<Long, LectureReviewQueryDto> lectureReviewQueryDtoMap = lectureQueryRepository.findLectureReviewQueryDtoMap(lectureIds);
        Map<Long, LectureMentorQueryDto> lectureMentorQueryDtoMap = lectureQueryRepository.findLectureMentorQueryDtoMap(lectureIds);
        lectures.forEach(lectureResponse -> {

            LectureReviewQueryDto lectureReviewQueryDto = lectureReviewQueryDtoMap.get(lectureResponse.getId());
            if (lectureReviewQueryDto != null) {
                lectureResponse.setReviewCount(lectureReviewQueryDto.getReviewCount());
                lectureResponse.setScoreAverage(lectureReviewQueryDto.getScoreAverage());
            } else {
                lectureResponse.setReviewCount(0);
                lectureResponse.setScoreAverage(0);
            }

            LectureResponse.LectureMentorResponse lectureMentorResponse = lectureResponse.getLectureMentor();
            LectureMentorQueryDto lectureMentorQueryDto = lectureMentorQueryDtoMap.get(lectureMentorResponse.getMentorId());
            if (lectureMentorQueryDto != null) {
                lectureMentorResponse.setLectureCount(lectureMentorQueryDto.getLectureCount());
                lectureMentorResponse.setReviewCount(lectureMentorQueryDto.getReviewCount());
            } else {
                lectureMentorResponse.setLectureCount(0);
                lectureMentorResponse.setReviewCount(0);
            }

            // 로그인한 경우 - 좋아요 여부 표시
            setPicked(user, lectureResponse.getId(), lectureResponse);
        });

//        lectures.forEach(lectureResponse -> {
//            setLectureReview(lectureResponse);
//            setLectureMentor(lectureResponse);
//        });

        return lectures;
    }

    private void setPicked(User user, Long lectureId, LectureResponse lectureResponse) {

        if (user == null) {
            return;
        }

        // TODO - flatMap
        Optional.ofNullable(menteeRepository.findByUser(user)).ifPresent(mentee -> {
            pickRepository.findByMenteeAndLectureId(mentee, lectureId)
                    // consumer
                    .ifPresent(pick -> lectureResponse.setPicked(true));
        });
    }

    private void setLectureReview(LectureResponse lectureResponse) {

        Lecture lecture = getLecture(lectureResponse.getId());

        List<Review> reviews = reviewRepository.findByLectureAndEnrollmentIsNotNull(lecture);
        lectureResponse.setReviewCount(reviews.size());
        OptionalDouble scoreAverage = reviews.stream().map(Review::getScore).mapToInt(Integer::intValue).average();
        lectureResponse.setScoreAverage(scoreAverage.isPresent() ? scoreAverage.getAsDouble() : 0);

    }

    private void setLectureMentor(LectureResponse lectureResponse) {

        Mentor mentor = getLecture(lectureResponse.getId()).getMentor();
        List<Lecture> lectures = lectureRepository.findByMentor(mentor);

        LectureResponse.LectureMentorResponse lectureMentorResponse = lectureResponse.getLectureMentor();
        lectureMentorResponse.setLectureCount(lectures.size());
        lectureMentorResponse.setReviewCount(reviewRepository.countByLectureInAndEnrollmentIsNotNull(lectures));
        lectureResponse.setLectureMentor(lectureMentorResponse);
    }

    @Transactional
    @Override
    public Lecture createLecture(User user, LectureCreateRequest lectureCreateRequest) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));

        // TODO - 유효성 -> 해당 유저의 강의 갯수 제한?
        // TODO - Lecture:toEntity
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
        return lectureRepository.save(lecture);
    }

    @Transactional
    @Override
    public void updateLecture(User user, Long lectureId, LectureUpdateRequest lectureUpdateRequest) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));

        Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        // 등록된 적 있는 강의면 수정 불가
        if (enrollmentRepository.countByLecture(lecture) > 0) {
            // TODO - 예외 처리
            throw new RuntimeException("등록된 강의는 수정이 불가합니다.");
        }

        // TODO - Lecture:update
        lecture.update(lectureUpdateRequest);
        for (LectureUpdateRequest.LecturePriceUpdateRequest lecturePriceUpdateRequest : lectureUpdateRequest.getLecturePrices()) {
            lecture.addPrice(lecturePriceUpdateRequest.toEntity(null));
        }
        for (LectureUpdateRequest.LectureSubjectUpdateRequest lectureSubjectUpdateRequest : lectureUpdateRequest.getLectureSubjects()) {
            Subject subject = subjectRepository.findById(lectureSubjectUpdateRequest.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException(SUBJECT));
            LectureSubject lectureSubject = LectureSubject.builder()
                    .lecture(null)
                    .subject(subject)
                    .build();
            lecture.addSubject(lectureSubject);
        }

        /*
        Hibernate: select user0_.user_id as user_id1_17_, user0_.created_at as created_2_17_, user0_.updated_at as updated_3_17_, user0_.bio as bio4_17_, user0_.deleted as deleted5_17_, user0_.deleted_at as deleted_6_17_, user0_.email as email7_17_, user0_.email_verified as email_ve8_17_, user0_.email_verified_at as email_ve9_17_, user0_.email_verify_token as email_v10_17_, user0_.gender as gender11_17_, user0_.image as image12_17_, user0_.name as name13_17_, user0_.nickname as nicknam14_17_, user0_.password as passwor15_17_, user0_.phone_number as phone_n16_17_, user0_.provider as provide17_17_, user0_.provider_id as provide18_17_, user0_.role as role19_17_, user0_.username as usernam20_17_, user0_.zone as zone21_17_ from user user0_ where user0_.username=?
        Hibernate: select user0_.user_id as user_id1_17_, user0_.created_at as created_2_17_, user0_.updated_at as updated_3_17_, user0_.bio as bio4_17_, user0_.deleted as deleted5_17_, user0_.deleted_at as deleted_6_17_, user0_.email as email7_17_, user0_.email_verified as email_ve8_17_, user0_.email_verified_at as email_ve9_17_, user0_.email_verify_token as email_v10_17_, user0_.gender as gender11_17_, user0_.image as image12_17_, user0_.name as name13_17_, user0_.nickname as nicknam14_17_, user0_.password as passwor15_17_, user0_.phone_number as phone_n16_17_, user0_.provider as provide17_17_, user0_.provider_id as provide18_17_, user0_.role as role19_17_, user0_.username as usernam20_17_, user0_.zone as zone21_17_ from user user0_ where user0_.username=?
        Hibernate: select mentor0_.mentor_id as mentor_id1_15_, mentor0_.created_at as created_2_15_, mentor0_.updated_at as updated_3_15_, mentor0_.specialist as speciali4_15_, mentor0_.subjects as subjects5_15_, mentor0_.user_id as user_id6_15_ from mentor mentor0_ where mentor0_.user_id=?
        Hibernate: select lecture0_.lecture_id as lecture_1_6_, lecture0_.created_at as created_2_6_, lecture0_.updated_at as updated_3_6_, lecture0_.content as content4_6_, lecture0_.difficulty_type as difficul5_6_, lecture0_.introduce as introduc6_6_, lecture0_.sub_title as sub_titl7_6_, lecture0_.thumbnail as thumbnai8_6_, lecture0_.title as title9_6_, lecture0_.mentor_id as mentor_i10_6_ from lecture lecture0_ where lecture0_.mentor_id=? and lecture0_.lecture_id=?
        Hibernate: select lecturepri0_.lecture_id as lecture10_7_1_, lecturepri0_.lecture_price_id as lecture_1_7_1_, lecturepri0_.lecture_price_id as lecture_1_7_0_, lecturepri0_.created_at as created_2_7_0_, lecturepri0_.updated_at as updated_3_7_0_, lecturepri0_.group_number as group_nu4_7_0_, lecturepri0_.is_group as is_group5_7_0_, lecturepri0_.lecture_id as lecture10_7_0_, lecturepri0_.pertime_cost as pertime_6_7_0_, lecturepri0_.pertime_lecture as pertime_7_7_0_, lecturepri0_.total_cost as total_co8_7_0_, lecturepri0_.total_time as total_ti9_7_0_ from lecture_price lecturepri0_ where lecturepri0_.lecture_id=?
        Hibernate: select lecturesub0_.lecture_id as lecture_6_8_1_, lecturesub0_.lecture_subject_id as lecture_1_8_1_, lecturesub0_.lecture_subject_id as lecture_1_8_0_, lecturesub0_.created_at as created_2_8_0_, lecturesub0_.updated_at as updated_3_8_0_, lecturesub0_.kr_subject as kr_subje4_8_0_, lecturesub0_.lecture_id as lecture_6_8_0_, lecturesub0_.parent as parent5_8_0_ from lecture_subject lecturesub0_ where lecturesub0_.lecture_id=?
        Hibernate: insert into lecture_price (created_at, group_number, is_group, lecture_id, pertime_cost, pertime_lecture, total_cost, total_time) values (?, ?, ?, ?, ?, ?, ?, ?)
        Hibernate: insert into lecture_subject (created_at, kr_subject, lecture_id, parent) values (?, ?, ?, ?)
        Hibernate: update lecture set updated_at=?, content=?, difficulty_type=?, introduce=?, sub_title=?, thumbnail=?, title=?, mentor_id=? where lecture_id=?
        Hibernate: delete from lecture_system_type where lecture_id=?
        Hibernate: insert into lecture_system_type (lecture_id, system_types) values (?, ?)
        Hibernate: delete from lecture_price where lecture_price_id=?
        Hibernate: delete from lecture_subject where lecture_subject_id=?
         */
    }

    // TODO - CHECK : 리팩토링
    @Transactional
    @Override
    public void deleteLecture(Lecture lecture) {

        enrollmentRepository.findByLecture(lecture).forEach(enrollment -> {
            enrollmentService.deleteEnrollment(enrollment);
        });

        // pick
        pickRepository.deleteByLecture(lecture);

        // TODO - CHECK : vs delete(lecture);
        // lecture_price
        // lecture_subject
        // lecture_system_type
        lectureRepository.delete(lecture);
        // lectureRepository.deleteById(lectureId);
    }

    @Transactional
    @Override
    public void deleteLecture(User user, Long lectureId) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));

        Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        deleteLecture(lecture);
    }

    @Override
    public void approve(User user, Long lectureId) {

        User _user = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(USER));

        if (!_user.getRole().equals(ADMIN)) {
            throw new UnauthorizedException(ADMIN);
        }

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        lecture.approve();
    }

    @Override
    public void open(User user, Long lectureId) {

        User _user = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(USER));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        // TODO - CHECK
        if (!lecture.getMentor().getUser().equals(_user)) {
            throw new UnauthorizedException();
        }
        lecture.open();
    }

    @Override
    public void close(User user, Long lectureId) {

        User _user = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(USER));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE));

        // TODO - CHECK
        if (!lecture.getMentor().getUser().equals(_user)) {
            throw new UnauthorizedException();
        }
        lecture.close();
    }

}
