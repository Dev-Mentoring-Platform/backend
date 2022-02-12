package com.project.mentoridge.modules.upload.respository;

import com.project.mentoridge.modules.upload.vo.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface FileRepository extends JpaRepository<File, Long> {

    File findByUuid(String uuid);
}
