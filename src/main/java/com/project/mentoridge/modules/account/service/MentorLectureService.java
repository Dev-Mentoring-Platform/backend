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
        return new EachLectureResponse(lecturePrice, lecturePrice.getLecture());
    }

    public Page<EachLectureResponse> getEachLectureResponses(Long mentorId, Integer page) {
        Mentor mentor = getMentor(mentorRepository, mentorId);
        Page<EachLectureResponse> lecturePrices = lectureSearchRepository.findLecturePricesByMentor(mentor, getPageRequest(page))
                .map(lecturePrice -> new EachLectureResponse(lecturePrice, lecturePrice.getLecture()));

        // ????????? ?????? ?????????
        // - ???????????? MAP ????????? ??????
        List<Long> lectureIds = lecturePrices.stream().map(EachLectureResponse::getLectureId).collect(Collectors.toList());
        List<Long> lecturePriceIds = lecturePrices.stream().map(lecturePrice -> lecturePrice.getLecturePrice().getLecturePriceId()).collect(Collectors.toList());

        // 2022.04.18 - lecturePriceId ???????????? enrollmentCount
        Map<Long, Long> lectureEnrollmentQueryDtoMap = lectureQueryRepository.findLectureEnrollmentQueryDtoMap(lecturePriceIds);
        // lecturePriceId ??????
        Map<Long, Long> lecturePickQueryDtoMap = lectureQueryRepository.findLecturePickQueryDtoMap(lecturePriceIds);

        // lectureId, lecturePriceId ??????
        Map<Long, LectureReviewQueryDto> lectureReviewQueryDtoMap = lectureQueryRepository.findLectureReviewQueryDtoMap(lectureIds, lecturePriceIds);
        // lectureId ??????
        Map<Long, LectureMentorQueryDto> lectureMentorQueryDtoMap = lectureQueryRepository.findLectureMentorQueryDtoMap(lectureIds);

        lecturePrices.forEach(lectureResponse -> {

            Long lecturePriceId = lectureResponse.getLecturePriceId();

            if (lectureEnrollmentQueryDtoMap.size() != 0 && lectureEnrollmentQueryDtoMap.get(lecturePriceId) != null) {
                lectureResponse.setEnrollmentCount(lectureEnrollmentQueryDtoMap.get(lecturePriceId));
            }

            if (lecturePickQueryDtoMap.size() != 0 && lecturePickQueryDtoMap.get(lecturePriceId) != null) {
                lectureResponse.setPickCount(lecturePickQueryDtoMap.get(lecturePriceId));
            }

            LectureReviewQueryDto lectureReviewQueryDto = null;
            if (lectureReviewQueryDtoMap.size() != 0 && lectureReviewQueryDtoMap.get(lecturePriceId) != null) {
                lectureReviewQueryDto = lectureReviewQueryDtoMap.get(lecturePriceId);
            }
            if (lectureReviewQueryDto != null) {
                lectureResponse.setReviewCount(lectureReviewQueryDto.getReviewCount());
                lectureResponse.setScoreAverage(lectureReviewQueryDto.getScoreAverage());
            }
            /*
            else {
                lectureResponse.setReviewCount(null);
                lectureResponse.setScoreAverage(null);
            }*/

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
