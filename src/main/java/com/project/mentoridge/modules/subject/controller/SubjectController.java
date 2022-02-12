package com.project.mentoridge.modules.subject.controller;

import com.project.mentoridge.modules.subject.controller.response.LearningKindResponse;
import com.project.mentoridge.modules.subject.controller.response.SubjectResponse;
import com.project.mentoridge.modules.subject.service.SubjectService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"SubjectController"})
@RequiredArgsConstructor
@RestController
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping(value = "/api/learningKinds", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLearningKinds() {
        List<LearningKindResponse> learningKinds = subjectService.getLearningKindResponses();
        return ResponseEntity.ok(learningKinds);
    }

    @GetMapping(value = "/api/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSubjects() {
        List<SubjectResponse> subjects = subjectService.getSubjectResponses();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping(value = "/api/subjects/{learning_kind_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSubjects(@PathVariable(name = "learning_kind_id") Long learningKindId) {
        List<SubjectResponse> subjects = subjectService.getSubjectResponses(learningKindId);
        return ResponseEntity.ok(subjects);
    }

}
