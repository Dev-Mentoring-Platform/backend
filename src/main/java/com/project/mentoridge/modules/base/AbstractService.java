package com.project.mentoridge.modules.base;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.MENTEE;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.MENTOR;

public abstract class AbstractService {

    protected static final Integer PAGE_SIZE = 10;

    protected static PageRequest getPageRequest(Integer page) {
        return PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").ascending());
    }

    protected static Mentor getMentor(MentorRepository mentorRepository, User user) {
        return Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new EntityNotFoundException(MENTOR));
    }

    protected static Mentor getMentor(MentorRepository mentorRepository, Long mentorId) {
        return mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(MENTOR));
    }

    protected static Mentee getMentee(MenteeRepository menteeRepository, User user) {
        return Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new EntityNotFoundException(MENTEE));
    }

    protected static Mentee getMentee(MenteeRepository menteeRepository, Long menteeId) {
        return menteeRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException(MENTEE));
    }
}
