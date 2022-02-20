package com.project.mentoridge.modules.purchase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface _CancellationRepository {
        // extends JpaRepository<Cancellation, Long> {

//    Cancellation findByEnrollment(Enrollment enrollment);
//    Cancellation findByEnrollmentId(Long enrollmentId);
//
//    @Transactional
//    void deleteByEnrollment(Enrollment enrollment);
}
