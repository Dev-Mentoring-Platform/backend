package com.project.mentoridge.modules.log.component.tracer;

import lombok.Getter;

@Getter
public class TraceStatus {
// 로그를 시작할 때의 상태 정보
    private TraceId traceId;
    private Long startTimeMs;
    private String message;

    public TraceStatus(TraceId traceId, Long startTimeMs, String message) {
        this.traceId = traceId;
        this.startTimeMs = startTimeMs;
        this.message = message;
    }
}
