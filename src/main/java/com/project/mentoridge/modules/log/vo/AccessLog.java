package com.project.mentoridge.modules.log.vo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter //@Setter
@Document(collection = "logs")
public class AccessLog {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    // TODO - sessionId를 왜 저장해야하는가
    private String sessionId;
    private String osType;
    private String accessPath;
    private String ip;
    private String lastAccessAt;

    private Long userId;
    private String username;
    private String loginAt;

    @Builder(access = AccessLevel.PUBLIC)
    public AccessLog(String sessionId, String osType, String accessPath, String ip, String lastAccessAt, Long userId, String username, String loginAt) {
        this.sessionId = sessionId;
        this.osType = osType;
        this.accessPath = accessPath;
        this.ip = ip;
        this.lastAccessAt = lastAccessAt;
        this.userId = userId;
        this.username = username;
        this.loginAt = loginAt;
    }

}
