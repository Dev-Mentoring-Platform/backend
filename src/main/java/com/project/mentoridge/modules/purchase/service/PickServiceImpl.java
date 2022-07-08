package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.lecture.controller.response.SimpleEachLectureResponse;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureQueryRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.dto.LectureReviewQueryDto;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.PickLogService;
import com.project.mentoridge.modules.purchase.controller.response.PickWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.purchase.repository.PickQueryRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.vo.Pick;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.LECTURE_PRICE;
import static com.project.mentoridge.modules.purchase.vo.Pick.buildPick;

@Service
@Transactional
@RequiredArgsConstructor
public class PickServiceImpl extends AbstractService implements PickService {

    private final PickRepository pickRepository;
    private final PickQueryRepository pickQueryRepository;

    private final MenteeRepository menteeRepository;
    private final LectureRepository lectureRepository;
    private final LecturePriceRepository lecturePriceRepository;
    private final LectureQueryRepository lectureQueryRepository;

    private final PickLogService pickLogService;

        private Lecture getLecture(Long lectureId) {
            return lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new EntityNotFoundException(LECTURE));
        }

        private Page<Pick> getPicks(User menteeUser, Integer page) {
            Mentee mentee = getMentee(menteeRepository, menteeUser);
            return pickRepository.findByMentee(mentee, getPageRequest(page));
        }

    @Transactional(readOnly = true)
    @Override
    public Page<PickWithSimpleEachLectureResponse> getPickWithSimpleEachLectureResponses(User menteeUser, Integer page) {
        Mentee mentee = getMentee(menteeRepository, menteeUser);
        Page<PickWithSimpleEachLectureResponse> picks = pickQueryRepository.findPicks(mentee, getPageRequest(page));

        List<Long> lectureIds = picks.stream().map(pick -> pick.getLecture().getId()).collect(Collectors.toList());
        List<Long> lecturePriceIds = picks.stream().map(pick -> pick.getLecture().getLecturePrice().getLecturePriceId()).collect(Collectors.toList());

        // lecturePriceId 기준
        Map<Long, Long> lecturePickQueryDtoMap = lectureQueryRepository.findLecturePickQueryDtoMap(lecturePriceIds);
        // lectureId 기준
        Map<Long, LectureReviewQueryDto> lectureReviewQueryDtoMap = lectureQueryRepository.findLectureReviewQueryDtoMap(lectureIds, lecturePriceIds);
        picks.forEach(pick -> {

            SimpleEachLectureResponse lectureResponse = pick.getLecture();

            Long lecturePriceId = pick.getLecture().getLecturePrice().getLecturePriceId();

            if (lecturePickQueryDtoMap.size() != 0 && lecturePickQueryDtoMap.get(lecturePriceId) != null) {
                lectureResponse.setPickCount(lecturePickQueryDtoMap.get(lecturePriceId));
            }

            LectureReviewQueryDto lectureReviewQueryDto;
            if (lectureReviewQueryDtoMap.size() != 0 && lectureReviewQueryDtoMap.get(lecturePriceId) != null) {

                lectureReviewQueryDto = lectureReviewQueryDtoMap.get(lecturePriceId);
                if (lectureReviewQueryDto != null) {
                    lectureResponse.setScoreAverage(lectureReviewQueryDto.getScoreAverage());
                }
            }

        });
        return picks;
    }

    @Override
    public Long createPick(User menteeUser, Long lectureId, Long lecturePriceId) {

        Mentee mentee = getMentee(menteeRepository, menteeUser);
        Lecture lecture = getLecture(lectureId);
        LecturePrice lecturePrice = lecturePriceRepository.findByLectureAndId(lecture, lecturePriceId)
                .orElseThrow(() -> new EntityNotFoundException(LECTURE_PRICE));

        Optional<Pick> pick = pickRepository.findByMenteeAndLectureAndLecturePrice(mentee, lecture, lecturePrice);
        if (pick.isPresent()) {
            Pick _pick = pick.get();
            _pick.delete(menteeUser, pickLogService);
            pickRepository.delete(_pick);
            return null;
        } else {
            Pick saved = pickRepository.save(buildPick(mentee, lecture, lecturePrice));
            pickLogService.insert(menteeUser, saved);
            return saved.getId();
        }
    }

    @Override
    public void deleteAllPicks(User menteeUser) {

        Mentee mentee = getMentee(menteeRepository, menteeUser);
        // TODO - batch
        pickRepository.deleteByMentee(mentee);
        pickLogService.deleteAll(menteeUser);
    }
}
