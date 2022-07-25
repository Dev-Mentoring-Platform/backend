package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.InvalidInputException;
import com.project.mentoridge.modules.account.controller.request.UserImageUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserPasswordUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserQuitRequest;
import com.project.mentoridge.modules.account.controller.request.UserUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.UserResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.LikingRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import com.project.mentoridge.modules.log.component.UserLogService;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService extends AbstractService {

    private final UserRepository userRepository;
    private final UserLogService userLogService;
    private final MentorService mentorService;
    private final MenteeService menteeService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final NotificationRepository notificationRepository;
    private final MessageRepository messageRepository;
    private final InquiryRepository inquiryRepository;
    private final LikingRepository likingRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

        private User getUser(Long userId) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException(USER));
        }

        private Page<User> getUsers(Integer page) {
            return userRepository.findAll(getPageRequest(page));
        }

    public Page<UserResponse> getUserResponses(Integer page) {
        return getUsers(page).map(UserResponse::new);
    }

    public UserResponse getUserResponse(Long userId) {
        return new UserResponse(getUser(userId));
    }

    public UserResponse getUserResponse(User user) {
        return getUserResponse(user.getId());
    }

    @Transactional
    public void updateUser(User user, UserUpdateRequest userUpdateRequest) {
        user = getUser(user.getId());
        user.update(userUpdateRequest, userLogService);
    }

    // TODO - Admin인 경우
    @Transactional
    public void deleteUser(User user, UserQuitRequest userQuitRequest) {

        user = getUser(user.getId());
        boolean match = bCryptPasswordEncoder.matches(userQuitRequest.getPassword(), user.getPassword());
        if (!match) {
            throw new InvalidInputException("잘못된 비밀번호입니다.");
        }

        if (user.getRole() == RoleType.MENTOR) {
            mentorService.deleteMentor(user);
        }
        menteeService.deleteMentee(user);

        // TODO - 스케줄러 ?
        // inquiry 삭제
        // inquiryRepository.deleteByUser(user);
        // notification 삭제
        notificationRepository.deleteByUser(user);
        // message 삭제
        messageRepository.deleteBySender(user);
        // liking 삭제
        likingRepository.deleteByUser(user);
        // comment 삭제
        commentRepository.deleteByUser(user);
        // post 삭제
        postRepository.deleteByUser(user);

        user.quit(userQuitRequest.getReason(), userLogService);
        // 로그아웃
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Transactional
    public void updateUserPassword(User user, UserPasswordUpdateRequest userPasswordUpdateRequest) {

        user = getUser(user.getId());
        if (!user.isEmailVerified() || user.isDeleted()) {
            throw new EntityNotFoundException(USER);
        }

        if (!bCryptPasswordEncoder.matches(userPasswordUpdateRequest.getPassword(), user.getPassword())) {
            throw new InvalidInputException("잘못된 비밀번호입니다.");
        }
        user.updatePassword(bCryptPasswordEncoder.encode(userPasswordUpdateRequest.getNewPassword()), userLogService);
    }

    @Transactional
    public void updateUserImage(User user, UserImageUpdateRequest userImageUpdateRequest) {
        user = getUser(user.getId());
        if (!user.isEmailVerified() || user.isDeleted()) {
            throw new EntityNotFoundException(USER);
        }
        user.updateImage(userImageUpdateRequest.getImage(), userLogService);
    }

    @Transactional
    public void updateUserFcmToken(String username, String fcmToken) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER));

        Optional<User> hasFcmToken = userRepository.findByFcmToken(fcmToken);
        if (hasFcmToken.isPresent()) {
            User tokenUser = hasFcmToken.get();
            if (!tokenUser.getUsername().equals(username)) {
                // 기존 fcmToken 삭제
                tokenUser.updateFcmToken(null, userLogService);
                user.updateFcmToken(fcmToken, userLogService);
            }
        } else {
            user.updateFcmToken(fcmToken, userLogService);
        }
    }
}
