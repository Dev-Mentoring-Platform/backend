package com.project.mentoridge.modules.notice.repository;

import com.project.mentoridge.modules.notice.vo.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
