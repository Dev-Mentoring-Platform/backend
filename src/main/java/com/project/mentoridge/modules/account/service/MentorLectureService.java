package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.repository.LectureQueryRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSearchRepository;
import com.project.mentoridge.modules.lecture.repository.dto.LectureMentorQueryDto;
import com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentResponse;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTOR;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MentorLectureService extends AbstractService {

    private final MentorRepository mentorRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final LectureRepository lectureRepository;
    private final LectureSearchRepository lectureSearchRepository;
    private final LectureQueryRepository lectureQueryRepository;

        private Mentor getMentor(User user) {
            return Optional.ofNullable(mentorRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTOR));
        }

        private Mentor getMentor(Long mentorId) {
            return mentorRepository.findById(mentorId)
                    .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTOR));
        }

        private Lecture getLecture(Mentor mentor, Long lectureId) {
            return lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        }

    public Page<LectureResponse> getLectureResponses(User user, Integer page) {
        Mentor mentor = getMentor(user);
        return lectureSearchRepository.findLecturesWithEnrollmentCountByMentor(mentor, getPageRequest(page));
    }

    public Page<LectureResponse> getLectureResponses(Long mentorId, Integer page) {
        Mentor mentor = getMentor(mentorId);
        return lectureRepository.findByMentor(mentor, getPageRequest(page)).map(LectureResponse::new);
    }

    public LecturePriceWithLectureResponse getLectureResponsePerLecturePrice(Long mentorId, Long lectureId, Long lecturePriceId) {
        Mentor mentor = getMentor(mentorId);
        LecturePrice lecturePrice = lectureSearchRepository.findLecturePerLecturePriceByMentor(mentor, lectureId, lecturePriceId);
        return new LecturePriceWithLectureResponse(lecturePrice, lecturePrice.getLecture());
    }

    // TODO : LectureServiceImpl - MentorLectureService
    public Page<LecturePriceWithLectureResponse> getLectureResponsesPerLecturePrice(Long mentorId, Integer page) {

        Mentor mentor = getMentor(mentorId);
        Page<LecturePriceWithLectureResponse> lecturePrices = lectureSearchRepository.findLecturesPerLecturePriceByMentor(mentor, getPageRequest(page))
                .map(lecturePrice -> new LecturePriceWithLectureResponse(lecturePrice, lecturePrice.getLecture()));

        // 컬렉션 조회 최적화
        // - 컬렉션을 MAP 한방에 조회
        List<Long> lectureIds = lecturePrices.stream().map(LecturePriceWithLectureResponse::getLectureId).collect(Collectors.toList());
        List<Long> lecturePriceIds = lecturePrices.stream().map(lecturePrice -> lecturePrice.getLecturePrice().getLecturePriceId()).collect(Collectors.toList());

        // 2022.04.18 - lecturePriceId 기준으로 enrollmentCount
        Map<Long, Long> lectureEnrollmentQueryDtoMap = lectureQueryRepository.findLectureEnrollmentQueryDtoMap(lecturePriceIds);
        // lecturePriceId 기준
        Map<Long, Long> lecturePickQueryDtoMap = lectureQueryRepository.findLecturePickQueryDtoMap(lecturePriceIds);

        // lectureId 기준
        Map<Long, LectureReviewQueryDto> lectureReviewQueryDtoMap = lectureQueryRepository.findLectureReviewQueryDtoMap(lectureIds);
        // lectureId 기준
        Map<Long, LectureMentorQueryDto> lectureMentorQueryDtoMap = lectureQueryRepository.findLectureMentorQueryDtoMap(lectureIds);

        lecturePrices.forEach(lectureResponse -> {

            Long lectureId = lectureResponse.getLectureId();
            Long lecturePriceId = lectureResponse.getLecturePriceId();

            if (lectureEnrollmentQueryDtoMap.size() != 0 && lectureEnrollmentQueryDtoMap.get(lecturePriceId) != null) {
                lectureResponse.setEnrollmentCount(lectureEnrollmentQueryDtoMap.get(lecturePriceId));
            }

            if (lecturePickQueryDtoMap.size() != 0 && lecturePickQueryDtoMap.get(lecturePriceId) != null) {
                lectureResponse.setPickCount(lecturePickQueryDtoMap.get(lecturePriceId));
            }

            LectureReviewQueryDto lectureReviewQueryDto = null;
            if (lectureReviewQueryDtoMap.size() != 0 && lectureReviewQueryDtoMap.get(lectureId) != null) {
                lectureReviewQueryDto = lectureReviewQueryDtoMap.get(lectureId);
            }
            if (lectureReviewQueryDto != null) {
                lectureResponse.setReviewCount(lectureReviewQueryDto.getReviewCount());
                lectureResponse.setScoreAverage(lectureReviewQueryDto.getScoreAverage());
            } else {
                lectureResponse.setReviewCount(0);
                lectureResponse.setScoreAverage(0);
            }

        });
        return lecturePrices;
    }

        private Page<Enrollment> getEnrollmentsOfLecture(User user, Long lectureId, Integer page) {
            Mentor mentor = getMentor(user);
            Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
            return enrollmentRepository.findByLecture(lecture, getPageRequest(page));
        }

    public Page<EnrollmentResponse> getEnrollmentResponsesOfLecture(User user, Long lectureId, Integer page) {
        return getEnrollmentsOfLecture(user, lectureId, page).map(EnrollmentResponse::new);
    }

        private Page<Mentee> getMenteesOfLecture(User user, Long lectureId, Integer page) {
            Mentor mentor = getMentor(user);
            Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
            // TODO - fetch join
            return enrollmentRepository.findByLecture(lecture, getPageRequest(page))
                    .map(Enrollment::getMentee);
        }

    public Page<MenteeResponse> getMenteeResponsesOfLecture(User user, Long lectureId, Integer page) {
        return getMenteesOfLecture(user, lectureId, page).map(MenteeResponse::new);
    }

}
