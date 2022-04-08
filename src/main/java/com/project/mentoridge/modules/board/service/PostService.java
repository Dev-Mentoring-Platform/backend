package com.project.mentoridge.modules.board.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.controller.response.PostResponse;
import com.project.mentoridge.modules.board.repository.LikeRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.vo.Like;
import com.project.mentoridge.modules.board.vo.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.POST;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService extends AbstractService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    // TODO - Log : PostLogService

    private final LikeRepository likeRepository;

        private User getUser(String username) {
            return userRepository.findByUsername(username).orElseThrow(UnauthorizedException::new);
                    //.orElseThrow(() -> new EntityNotFoundException(USER));
        }

        private Post getPost(User user, Long postId) {
            return postRepository.findByUserAndId(user, postId)
                    .orElseThrow(() -> new EntityNotFoundException(POST));
        }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostResponses(User user, Integer page) {
        user = getUser(user.getUsername());
        return postRepository.findByUser(user, getPageRequest(page)).map(PostResponse::new);
    }

    @Transactional(readOnly = true)
    public PostResponse getPostResponse(User user, Long postId) {
        user = getUser(user.getUsername());
        Post post = getPost(user, postId);
        return new PostResponse(post);
    }

    public Post createPost(User user, PostCreateRequest createRequest) {

        user = getUser(user.getUsername());
        Post post = createRequest.toEntity(user);
        return postRepository.save(post);
    }

    public void updatePost(User user, Long postId, PostUpdateRequest updateRequest) {

        user = getUser(user.getUsername());
        Post post = getPost(user, postId);
        post.update(updateRequest);
    }

    public void deletePost(User user, Long postId) {

        // TODO - CHECK : userId
        user = getUser(user.getUsername());
        Post post = getPost(user, postId);
        postRepository.delete(post);
    }


    public void likePost(User user, Long postId) {

        user = getUser(user.getUsername());
        Post post = getPost(user, postId);

        Like like = likeRepository.findByUserAndPost(user, post);
        if (like == null) {
            likeRepository.save(Like.builder()
                    .user(user)
                    .post(post)
                    .build());
        } else {
            likeRepository.delete(like);
        }
    }
/*
    public void cancelPostLike(User user, Long postId) {

        user = getUser(user.getUsername());
        Post post = getPost(user, postId);

        likeRepository.findByUserAndPost(user, post)
                .ifPresent(likeRepository::delete);
    }*/

}
