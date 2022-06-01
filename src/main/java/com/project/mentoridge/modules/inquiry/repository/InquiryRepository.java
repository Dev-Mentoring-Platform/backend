package com.project.mentoridge.modules.inquiry.repository;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Transactional
    @Modifying
    void deleteByUser(User user);
}
