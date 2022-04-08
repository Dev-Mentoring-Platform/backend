package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.log.component.PickLogService;
import com.project.mentoridge.modules.purchase.controller.response.PickResponse;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.vo.Pick;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.PICK;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;
import static com.project.mentoridge.modules.purchase.vo.Pick.buildPick;

@Service
@Transactional
@RequiredArgsConstructor
public class PickServiceImpl extends AbstractService implements PickService {

    private final PickRepository pickRepository;
    private final MenteeRepository menteeRepository;
    private final LectureRepository lectureRepository;

    private final PickLogService pickLogService;

        private Mentee getMentee(User user) {
            return Optional.ofNullable(menteeRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTEE));
        }

        private Lecture getLecture(Long lectureId) {
            return lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        }

        private Page<Pick> getPicks(User user, Integer page) {
            // TODO - AuthAspect or Interceptor로 처리
            Mentee mentee = getMentee(user);
            return pickRepository.findByMentee(mentee, getPageRequest(page));
        }

    @Transactional(readOnly = true)
    @Override
    public Page<PickResponse> getPickResponses(User user, Integer page) {
        return getPicks(user, page).map(PickResponse::new);
    }

    @Override
    public Pick createPick(User user, Long lectureId) {

        Mentee mentee = getMentee(user);
        Lecture lecture = getLecture(lectureId);

        Pick saved = pickRepository.save(buildPick(mentee, lecture));
        pickLogService.insert(user, saved);
        return saved;
    }

    @Override
    public void deletePick(User user, Long pickId) {

        Mentee mentee = getMentee(user);
        Pick pick = pickRepository.findByMenteeAndId(mentee, pickId)
                .orElseThrow(() -> new EntityNotFoundException(PICK));

        pick.delete();
        pickLogService.delete(user, pick);
        pickRepository.delete(pick);
    }

    @Override
    public void deleteAllPicks(User user) {

        Mentee mentee = getMentee(user);
        // TODO - batch
        pickRepository.deleteByMentee(mentee);
    }
}
