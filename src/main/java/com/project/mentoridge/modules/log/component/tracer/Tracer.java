package com.project.mentoridge.modules.log.component.tracer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Tracer {

    public TraceStatus begin(String message) {
        TraceId traceId = new TraceId();
        long startTimeMs = System.currentTimeMillis();
        return new TraceStatus(traceId, startTimeMs, message);
    }

    public void end(TraceStatus status) {

    }

    public void exception(TraceStatus status, Exception e) {

    }
}
