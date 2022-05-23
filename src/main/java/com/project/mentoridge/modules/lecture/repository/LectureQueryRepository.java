package com.project.mentoridge.modules.lecture.repository;

import com.project.mentoridge.modules.lecture.repository.dto.LectureEnrollmentQueryDto;
import com.project.mentoridge.modules.lecture.repository.dto.LectureMentorQueryDto;
import com.project.mentoridge.modules.lecture.repository.dto.LecturePickQueryDto;
import com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class LectureQueryRepository {

    private final EntityManager em;


    public Map<Long, Long> findLectureEnrollmentQueryDtoMap(List<Long> lecturePriceIds) {
        List<LectureEnrollmentQueryDto> lectureEnrollments = em.createQuery("select new com.project.mentoridge.modules.lecture.repository.dto.LectureEnrollmentQueryDto(e.lecturePrice.id, count(e.id)) from Enrollment e " +
                "where e.checked = true and e.lecturePrice.id in :lecturePriceIds group by e.lecturePrice.id", LectureEnrollmentQueryDto.class)
                .setParameter("lecturePriceIds", lecturePriceIds).getResultList();
        return lectureEnrollments.stream()
                .collect(Collectors.toMap(LectureEnrollmentQueryDto::getLectureId, LectureEnrollmentQueryDto::getEnrollmentCount));
    }

    public Map<Long, Long> findLecturePickQueryDtoMap(List<Long> lecturePriceIds) {
        List<LecturePickQueryDto> lecturePicks = em.createQuery("select new com.project.mentoridge.modules.lecture.repository.dto.LecturePickQueryDto(p.lecturePrice.id, count(p.id)) from Pick p " +
                "where p.lecturePrice.id in :lecturePriceIds group by p.lecturePrice.id", LecturePickQueryDto.class)
                .setParameter("lecturePriceIds", lecturePriceIds).getResultList();
        return lecturePicks.stream().collect(Collectors.toMap(LecturePickQueryDto::getLectureId, LecturePickQueryDto::getPickCount));
    }

    /*
    SELECT e.lecture_id, e.lecture_price_id, COUNT(r.mentee_review_id), AVG(r.score) FROM mentee_review r
    INNER JOIN enrollment e ON r.enrollment_id = e.enrollment_id
    GROUP BY e.lecture_id, e.lecture_price_id;

    SELECT e.lecture_price_id, COUNT(r.mentee_review_id), AVG(r.score) FROM mentee_review r
    INNER JOIN enrollment e ON r.enrollment_id = e.enrollment_id
    GROUP BY  e.lecture_price_id;
     */
    public Map<Long, LectureReviewQueryDto> findLectureReviewQueryDtoMap(List<Long> lectureIds, List<Long> lecturePriceIds) {
        List<LectureReviewQueryDto> lectureReviews = em.createQuery("select new com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto(e.lecture.id, e.lecturePrice.id, count(r.id), avg(r.score)) from MenteeReview r " +
                        "inner join Enrollment e on r.enrollment.id = e.id " +
                        "where e.lecture.id in :lectureIds and e.lecturePrice.id in :lecturePriceIds " +
                        "group by e.lecture, e.lecturePrice", LectureReviewQueryDto.class)
                .setParameter("lectureIds", lectureIds)
                .setParameter("lecturePriceIds", lecturePriceIds)
                .getResultList();

        return lectureReviews.stream()
                .collect(Collectors.toMap(LectureReviewQueryDto::getLecturePriceId, lectureReviewQueryDto -> lectureReviewQueryDto));
    }

    public Optional<LectureReviewQueryDto> findLectureReviewQueryDto(Long lectureId, Long lecturePriceId) {
        return em.createQuery("select new com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto(e.lecture.id, e.lecturePrice.id, count(r.id), avg(r.score)) from MenteeReview r " +
                        "inner join Enrollment e on r.enrollment.id = e.id " +
                        "where e.lecture.id = :lectureId and e.lecturePrice.id = :lecturePriceId " +
                        "group by e.lecture, e.lecturePrice", LectureReviewQueryDto.class)
                .setParameter("lectureId", lectureId)
                .setParameter("lecturePriceId", lecturePriceId)
                .getResultList().stream().findAny();
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
