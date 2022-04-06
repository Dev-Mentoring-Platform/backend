package com.project.mentoridge.modules.board.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.POST;
import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.USER;

@Transactional
@RequiredArgsConstructor
@Service
public class CommentService extends AbstractService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

        private User getUser(String username) {
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException(USER));
        }

        private Post getPost(User user, Long postId) {
            return postRepository.findByUserAndId(user, postId)
                    .orElseThrow(() -> new EntityNotFoundException(POST));
        }

    // getComments

    public Comment createComment(User user, Long postId, CommentCreateRequest createRequest) {

        user = getUser(user.getUsername());
        Post post = getPost(user, postId);

        Comment comment = createRequest.toEntity(user, post);
        return commentRepository.save(comment);
    }

    public void updateComment() {

    }

    public void deleteComment() {

    }

}
