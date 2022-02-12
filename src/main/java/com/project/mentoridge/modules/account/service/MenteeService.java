package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;


@Service
@Transactional
@RequiredArgsConstructor
public class MenteeService extends AbstractService {

    private final MenteeRepository menteeRepository;

    private final PickRepository pickRepository;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;

    private Page<Mentee> getMentees(Integer page) {
        return menteeRepository.findAll(PageRequest.of(page - 1, PAGE_SIZE, Sort.by("id").ascending()));
    }

    @Transactional(readOnly = true)
    public Page<MenteeResponse> getMenteeResponses(Integer page) {
        return getMentees(page).map(MenteeResponse::new);
    }

    private Mentee getMentee(Long menteeId) {
        return menteeRepository.findById(menteeId).orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTEE));
    }

    @Transactional(readOnly = true)
    public MenteeResponse getMenteeResponse(Long menteeId) {
        return new MenteeResponse(getMentee(menteeId));
    }

    public void updateMentee(User user, MenteeUpdateRequest menteeUpdateRequest) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));

        mentee.update(menteeUpdateRequest);
    }

    public void deleteMentee(User user) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));

        // pick 삭제
        pickRepository.deleteByMentee(mentee);
        // enrollment 삭제
        enrollmentRepository.findAllByMenteeId(mentee.getId()).forEach(enrollment -> {
            enrollmentService.deleteEnrollment(enrollment);
        });
        menteeRepository.delete(mentee);
    }
}
