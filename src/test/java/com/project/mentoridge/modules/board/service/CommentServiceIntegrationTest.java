package com.project.mentoridge.modules.board.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.controller.request.CommentUpdateRequest;
import com.project.mentoridge.modules.board.controller.response.CommentResponse;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ServiceTest
class CommentServiceIntegrationTest {

    @Autowired
    CommentService commentService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    LoginService loginService;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    MenteeRepository menteeRepository;

    private User user1;
    private Mentee mentee1;
    private Mentor mentor1;
    private User user2;
    private Mentee mentee2;

    @BeforeEach
    void init() {

        saveAddress(addressRepository);
        saveSubject(subjectRepository);

        // user1 - mentor
        user1 = saveMentorUser(loginService, mentorService);
        mentee1 = menteeRepository.findByUser(user1);
        mentor1 = mentorRepository.findByUser(user1);

        // user2 - mentee
        user2 = saveMenteeUser(loginService);
        mentee2 = menteeRepository.findByUser(user2);
    }

    @Test
    void get_commentResponses() {

        // given
        Post post = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title")
                .content("post_content")
                .build());
        Comment comment1 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content1")
                .build());
        Comment comment2 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content2")
                .build());

        // when
        Page<CommentResponse> responses = commentService.getCommentResponses(user2, post.getId(), 1);
        // then
        assertThat(responses).hasSize(2);
        assertThat(responses).contains(new CommentResponse(comment1), new CommentResponse(comment2));

        for (CommentResponse response : responses) {

            if (response.getCommentId().equals(comment1.getId())) {
                assertResponse(response, comment1);
            } else if (response.getCommentId().equals(comment2.getId())) {
                assertResponse(response, comment2);
            } else {
                fail();
            }
        }

    }

    private void assertResponse(CommentResponse response, Comment comment) {
        assertThat(response).extracting("commentId").isEqualTo(comment.getId());
        assertThat(response).extracting("postId").isEqualTo(comment.getPost().getId());
        assertThat(response).extracting("userNickname").isEqualTo(comment.getUser().getNickname());
        assertThat(response).extracting("userImage").isEqualTo(comment.getUser().getImage());
        assertThat(response).extracting("content").isEqualTo(comment.getContent());
        assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(comment.getCreatedAt()));
    }

    @DisplayName("댓글 추가")
    @Test
    void create_comment() {

        // given
        Post post = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title")
                .content("post_content")
                .build());
        Comment comment1 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content1")
                .build());
        Comment comment2 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content2")
                .build());

        // when
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("comment_content3")
                .build();
        Comment created = commentService.createComment(user2, post.getId(), commentCreateRequest);

        // then
        Page<Comment> comments = commentRepository.findByPost(post, Pageable.unpaged());
        assertThat(comments).hasSize(3);
        assertThat(comments).contains(comment1, comment2, created);
        assertThat(created.getContent()).isEqualTo(commentCreateRequest.getContent());
    }

    @DisplayName("댓글 수정")
    @Test
    void update_comment() {

        // given
        Post post = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title")
                .content("post_content")
                .build());
        Comment comment1 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content1")
                .build());
        Comment comment2 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content2")
                .build());

        // when
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequest.builder()
                .content("updated_comment_content2")
                .build();
        commentService.updateComment(user2, post.getId(), comment2.getId(), commentUpdateRequest);

        // then
        Page<Comment> comments = commentRepository.findByPost(post, Pageable.unpaged());
        assertThat(comments).hasSize(2);
        assertThat(comments).contains(comment1, comment2);
        assertThat(comment2.getContent()).isEqualTo(commentUpdateRequest.getContent());
    }

    @Test
    void update_comment_not_by_commentWriter() {

        // given
        Post post = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title")
                .content("post_content")
                .build());
        Comment comment1 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content1")
                .build());
        // when
        // then
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequest.builder()
                .content("updated_comment_content1")
                .build();
        assertThrows(UnauthorizedException.class,
                () -> commentService.updateComment(user1, post.getId(), comment1.getId(), commentUpdateRequest));
    }

    @Test
    void update_not_existed_comment() {

        // given
        Post post = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title")
                .content("post_content")
                .build());
        Comment comment1 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content1")
                .build());
        // when
        // then
        CommentUpdateRequest commentUpdateRequest = CommentUpdateRequest.builder()
                .content("updated_comment_content1")
                .build();
        assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(user1, post.getId(), 10000L, commentUpdateRequest));
    }

    @DisplayName("댓글 삭제")
    @Test
    void delete_comment() {

        // given
        Post post = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title")
                .content("post_content")
                .build());
        Comment comment1 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content1")
                .build());
        Comment comment2 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content2")
                .build());

        // when
        commentService.deleteComment(user2, post.getId(), comment2.getId());

        // then
        Page<Comment> comments = commentRepository.findByPost(post, Pageable.unpaged());
        assertThat(comments).hasSize(1);
        assertThat(comments).contains(comment1);
        assertFalse(commentRepository.findById(comment2.getId()).isPresent());
    }

    @Test
    void delete_comment_not_by_commentWriter() {

        // given
        Post post = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title")
                .content("post_content")
                .build());
        Comment comment1 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content1")
                .build());
        Comment comment2 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content2")
                .build());

        // when
        // then
        assertThrows(UnauthorizedException.class,
                () -> commentService.deleteComment(user1, post.getId(), comment2.getId()));
    }

    @Test
    void delete_not_existed_comment() {

        // given
        Post post = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title")
                .content("post_content")
                .build());
        Comment comment1 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content1")
                .build());
        Comment comment2 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post)
                .content("comment_content2")
                .build());

        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> commentService.deleteComment(user2, post.getId(), 10000L));
    }
}