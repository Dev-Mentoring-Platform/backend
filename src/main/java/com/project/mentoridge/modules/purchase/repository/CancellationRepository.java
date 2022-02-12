package com.project.mentoridge.modules.purchase.repository;

import com.project.mentoridge.modules.purchase.vo.Cancellation;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface CancellationRepository extends JpaRepository<Cancellation, Long> {

    Cancellation findByEnrollment(Enrollment enrollment);
    Cancellation findByEnrollmentId(Long enrollmentId);

    @Transactional
    void deleteByEnrollment(Enrollment enrollment);
}
