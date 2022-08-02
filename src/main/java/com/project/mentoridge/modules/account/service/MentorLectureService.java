package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
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
import java.util.stream.Collectors;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MentorLectureService extends AbstractService {

    private final MentorRepository mentorRepository;
    private final EnrollmentRepository enrollmentRepository;

    private final LectureRepository lectureRepository;
    private final LectureSearchRepository lectureSearchRepository;
    private final LectureQueryRepository lectureQueryRepository;


    public Page<LectureResponse> getLectureResponses(User mentorUser, Integer page) {
        Mentor mentor = getMentor(mentorRepository, mentorUser);
        return lectureSearchRepository.findLecturesWithEnrollmentCountByMentor(mentor, getPageRequest(page));
    }

    public Page<LectureResponse> getLectureResponses(Long mentorId, Integer page) {
        Mentor mentor = getMentor(mentorRepository, mentorId);
        return lectureRepository.findByMentor(mentor, getPageRequest(page)).map(LectureResponse::new);
    }

    public EachLectureResponse getEachLectureResponse(Long mentorId, Long lectureId, Long lecturePriceId) {
        Mentor mentor = getMentor(mentorRepository, mentorId);
        LecturePrice lecturePrice = lectureSearchRepository.findLecturePriceByMentor(mentor, lectureId, lecturePriceId);
        if (lecturePrice == null) {
            return null;
        }
        return new EachLectureResponse(lecturePrice, lecturePrice.getLecture());
    }

    public Page<EachLectureResponse> getEachLectureResponses(Long mentorId, Integer page) {
        Mentor mentor = getMentor(mentorRepository, mentorId);
        Page<EachLectureResponse> lecturePrices = lectureSearchRepository.findLecturePricesByMentor(mentor, getPageRequest(page))
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

        lecturePrices.forEach(lectureResponse -> {

            Long lectureId = lectureResponse.getLectureId();
            if (lectureMentorQueryDtoMap.size() != 0 && lectureMentorQueryDtoMap.get(mentorId) != null) {
                LectureMentorQueryDto lectureMentorQueryDto = lectureMentorQueryDtoMap.get(mentorId);
                lectureResponse.getLectureMentor().setMentorId(lectureMentorQueryDto.getMentorId());
                lectureResponse.getLectureMentor().setLectureCount(lectureMentorQueryDto.getLectureCount());
                lectureResponse.getLectureMentor().setReviewCount(lectureMentorQueryDto.getReviewCount());
            } else {
                lectureResponse.setLectureMentor(null);
            }

            Long lecturePriceId = lectureResponse.getLecturePrice().getLecturePriceId();

            if (lectureEnrollmentQueryDtoMap.size() != 0 && lectureEnrollmentQueryDtoMap.get(lecturePriceId) != null) {
                lectureResponse.setEnrollmentCount(lectureEnrollmentQueryDtoMap.get(lecturePriceId));
            } else {
                lectureResponse.setEnrollmentCount(0L);
            }

            if (lecturePickQueryDtoMap.size() != 0 && lecturePickQueryDtoMap.get(lecturePriceId) != null) {
                lectureResponse.setPickCount(lecturePickQueryDtoMap.get(lecturePriceId));
            } else {
                lectureResponse.setPickCount(0L);
            }

            if (lectureReviewQueryDtoMap.size() != 0 && lectureReviewQueryDtoMap.get(lecturePriceId) != null) {
                LectureReviewQueryDto lectureReviewQueryDto = lectureReviewQueryDtoMap.get(lecturePriceId);
                if (lectureReviewQueryDto != null) {
                    lectureResponse.setReviewCount(lectureReviewQueryDto.getReviewCount());
                    lectureResponse.setScoreAverage(lectureReviewQueryDto.getScoreAverage());
                } else {
                    lectureResponse.setReviewCount(0L);
                    lectureResponse.setScoreAverage(0.0);
                }
            }

        });
        return lecturePrices;
    }

        private Page<Enrollment> getEnrollmentsOfLecture(User mentorUser, Long lectureId, Integer page) {
            Mentor mentor = getMentor(mentorRepository, mentorUser);
            Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
            return enrollmentRepository.findByLecture(lecture, getPageRequest(page));
        }

    public Page<EnrollmentResponse> getEnrollmentResponsesOfLecture(User mentorUser, Long lectureId, Integer page) {
        return getEnrollmentsOfLecture(mentorUser, lectureId, page).map(EnrollmentResponse::new);
    }

        private Page<Mentee> getMenteesOfLecture(User mentorUser, Long lectureId, Integer page) {
            Mentor mentor = getMentor(mentorRepository, mentorUser);
            Lecture lecture = lectureRepository.findByMentorAndId(mentor, lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
            // TODO - fetch join
            return enrollmentRepository.findByLecture(lecture, getPageRequest(page))
                    .map(Enrollment::getMentee);
        }

    public Page<MenteeResponse> getMenteeResponsesOfLecture(User mentorUser, Long lectureId, Integer page) {
        return getMenteesOfLecture(mentorUser, lectureId, page).map(MenteeResponse::new);
    }

}
