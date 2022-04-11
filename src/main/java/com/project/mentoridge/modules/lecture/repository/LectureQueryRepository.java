package com.project.mentoridge.modules.lecture.repository;

import com.project.mentoridge.modules.lecture.repository.dto.LectureEnrollmentQueryDto;
import com.project.mentoridge.modules.lecture.repository.dto.LectureMentorQueryDto;
import com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class LectureQueryRepository {

    private final EntityManager em;

    /*
    SELECT lecture_id, COUNT(*) FROM enrollment
    WHERE lecture_id in () AND checked = 1
    GROUP BY lecture_id;
     */
    public Map<Long, Long> findLectureEnrollmentQueryDtoMap(List<Long> lectureIds) {
        List<LectureEnrollmentQueryDto> lectureEnrollments = em.createQuery("select new com.project.mentoridge.modules.lecture.repository.dto.LectureEnrollmentQueryDto(e.lecture.id, count(e.id)) from Enrollment e " +
                "where e.checked = true and e.lecture.id in :lectureIds group by e.lecture.id", LectureEnrollmentQueryDto.class)
                .setParameter("lectureIds", lectureIds).getResultList();
        return lectureEnrollments.stream()
                .collect(Collectors.toMap(LectureEnrollmentQueryDto::getLectureId, LectureEnrollmentQueryDto::getEnrollmentCount));
    }

    /*
    SELECT r.lecture_id, COUNT(r.review_id), ROUND(AVG(r.score), 2) FROM review r
    WHERE r.enrollment_id IS NOT NULL
    -- AND r.lecture_id = 22
    GROUP BY r.lecture_id
     */
    public Map<Long, LectureReviewQueryDto> findLectureReviewQueryDtoMap(List<Long> lectureIds) {
        List<LectureReviewQueryDto> lectureReviews = em.createQuery("select new com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto(r.lecture.id, count(r.id), avg(r.score)) from MenteeReview r " +
                "where r.lecture.id in :lectureIds group by r.lecture", LectureReviewQueryDto.class)
                .setParameter("lectureIds", lectureIds).getResultList();

        return lectureReviews.stream()
                .collect(Collectors.toMap(LectureReviewQueryDto::getLectureId, lectureReviewQueryDto -> lectureReviewQueryDto));
//        return lectureReviews.stream()
//                .collect(Collectors.groupingBy(LectureReviewQueryDto::getLectureId));
    }

    /*
    SELECT t.mentor_id, COUNT(DISTINCT l.lecture_id), COUNT(DISTINCT r.review_id) FROM lecture l
    INNER JOIN mentor t ON l.mentor_id = t.mentor_id
    LEFT JOIN review r ON l.lecture_id = r.lecture_id AND r.enrollment_id IS NOT NULL
    GROUP BY t.mentor_id
     */
    public Map<Long, LectureMentorQueryDto> findLectureMentorQueryDtoMap(List<Long> lectureIds) {
        List<LectureMentorQueryDto> lectureMentors = em.createQuery("select new com.project.mentoridge.modules.lecture.repository.dto.LectureMentorQueryDto(t.id, count(distinct l.id), count(distinct r.id)) from Lecture l " +
                "inner join Mentor t on l.mentor.id = t.id " +
                "left join MenteeReview r on l.id = r.lecture.id " +
                "where l.id in :lectureIds group by t.id", LectureMentorQueryDto.class).setParameter("lectureIds", lectureIds).getResultList();

        return lectureMentors.stream().collect(Collectors.toMap(LectureMentorQueryDto::getMentorId, lectureMentorQueryDto -> lectureMentorQueryDto));
    }
}
