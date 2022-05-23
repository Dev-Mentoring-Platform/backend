package com.project.mentoridge.modules.board.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.board.controller.request.ConentSearchRequest;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.controller.response.PostResponse;
import com.project.mentoridge.modules.board.repository.ContentSearchRepository;
import com.project.mentoridge.modules.board.repository.LikingRepository;
import com.project.mentoridge.modules.board.repository.PostQueryRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.vo.Liking;
import com.project.mentoridge.modules.board.vo.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.POST;

@Transactional
@RequiredArgsConstructor
@Service
public class PostService extends AbstractService {

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final ContentSearchRepository contentSearchRepository;
    // TODO - Log : PostLogService

    private final UserRepository userRepository;
    private final LikingRepository likingRepository;

        private User getUser(String username) {
            return userRepository.findByUsername(username).orElseThrow(UnauthorizedException::new);
                    //.orElseThrow(() -> new EntityNotFoundException(USER));
        }

        private Post getPost(User user, Long postId) {
            return postRepository.findByUserAndId(user, postId)
                    .orElseThrow(() -> new EntityNotFoundException(POST));
        }

        private Post getPost(Long postId) {
            return postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException(POST));
        }

        private void setCounts(Page<PostResponse> postResponses) {
            List<Long> postIds = postResponses.stream().map(postResponse -> postResponse.getPostId()).collect(Collectors.toList());
            Map<Long, Long> postCommentQueryDtoMap = postQueryRepository.findPostCommentQueryDtoMap(postIds);
            Map<Long, Long> postLikingQueryDtoMap = postQueryRepository.findPostLikingQueryDtoMap(postIds);

            postResponses.stream()
                    .forEach(postResponse -> {
                        Long postId = postResponse.getPostId();
                        if (postCommentQueryDtoMap.get(postId) != null) {
                            postResponse.setCommentCount(postCommentQueryDtoMap.get(postId));
                        }
                        if (postLikingQueryDtoMap.get(postId) != null) {
                            postResponse.setLikingCount(postLikingQueryDtoMap.get(postId));
                        }
                    });
        }

        private void setCount(PostResponse postResponse) {
            Long postId = postResponse.getPostId();
            Long commentCount = postQueryRepository.findPostCommentQueryDtoMap(postId);
            if (commentCount != null) {
                postResponse.setCommentCount(commentCount);
            } else {
                postResponse.setCommentCount(0L);
            }
            Long likingCount = postQueryRepository.findPostLikingQueryDtoMap(postId);
            if (likingCount != null) {
                postResponse.setLikingCount(likingCount);
            } else {
                postResponse.setLikingCount(0L);
            }

        }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostResponsesOfUser(User user, Integer page) {

        user = getUser(user.getUsername());
        Page<PostResponse> postResponses = postRepository.findByUser(user, getPageRequest(page)).map(PostResponse::new);

        setCounts(postResponses);
        return postResponses;
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostResponses(User user, Integer page) {

        user = getUser(user.getUsername());
        Page<PostResponse> postResponses = postRepository.findAll(getPageRequest(page)).map(PostResponse::new);
        setCounts(postResponses);
        return postResponses;
    }

    // Post, Comment + 닉네임(?) 둘 다 search
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostResponses(User user, ConentSearchRequest searchRequest, Integer page) {
        Page<PostResponse> postResponses = contentSearchRepository.findPostsSearchedByContent();

        setCounts(postResponses);
        return postResponses;
    }

    public PostResponse getPostResponse(User user, Long postId) {
        user = getUser(user.getUsername());
        Post post = getPost(postId);
        post.hit();

        PostResponse postResponse = new PostResponse(post);
        setCount(postResponse);
        return postResponse;
    }
/*
    @Transactional(readOnly = true)
    public PostResponse getPostResponseOfUser(User user, Long postId) {
        user = getUser(user.getUsername());
        Post post = getPost(user, postId);
        return new PostResponse(post);
    }*/

    // 댓글단 글 리스트
    @Transactional(readOnly = true)
    public Page<PostResponse> getCommentingPostResponses(User user, Integer page) {
        return postQueryRepository.findCommentingPosts(user.getId(), getPageRequest(page));
    }

    // 좋아요한 글 리스트
    @Transactional(readOnly = true)
    public Page<PostResponse> getLikingPostResponses(User user, Integer page) {
        return postQueryRepository.findLikingPosts(user.getId(), getPageRequest(page));
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

        Post post = getPost(postId);

        Liking liking = likingRepository.findByUserAndPost(user, post);
        if (liking == null) {
            likingRepository.save(Liking.builder()
                    .user(user)
                    .post(post)
                    .build());
        } else {
            likingRepository.delete(liking);
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
