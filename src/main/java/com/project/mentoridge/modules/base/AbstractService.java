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

    protected static PageRequest getPageDescRequest(Integer page) {
        return PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").descending());
    }

    protected static Mentor getMentor(MentorRepository mentorRepository, User mentorUser) {
        return Optional.ofNullable(mentorRepository.findByUser(mentorUser))
                .orElseThrow(() -> new EntityNotFoundException(MENTOR));
    }

    protected static Mentor getMentor(MentorRepository mentorRepository, Long mentorId) {
        return mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(MENTOR));
    }

    protected static Mentee getMentee(MenteeRepository menteeRepository, User menteeUser) {
        return Optional.ofNullable(menteeRepository.findByUser(menteeUser))
                .orElseThrow(() -> new EntityNotFoundException(MENTEE));
    }

    protected static Mentee getMentee(MenteeRepository menteeRepository, Long menteeId) {
        return menteeRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException(MENTEE));
    }
/*
    public static void main(String[] args) {

        PageRequest request1 = getPageRequest(1);
        PageRequest request2 = getPageRequest(1);
        System.out.println(request1 == request2);
        System.out.println(request1.equals(request2));
    }*/
}
