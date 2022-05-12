package com.project.mentoridge.modules.log.repository;

import com.project.mentoridge.modules.log.vo.Log;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Profile({"prod"})
@Transactional(readOnly = true)
@Repository
public interface MongoLogRepository extends MongoRepository<Log, String>, LogRepository {
}
