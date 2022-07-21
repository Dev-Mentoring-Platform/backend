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
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.controller.response.PostResponse;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.LikingRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Liking;
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class PostServiceIntegrationTest {

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    LikingRepository likingRepository;

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

    private Post post1;
    private Comment comment1;
    private Comment comment2;
    private Liking liking1;

    private Post post2;
    // private Comment comment3;
    private Post post3;
    private Liking liking2;


    @BeforeAll
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

        post1 = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title1")
                .content("post_content1")
                .build());
        comment1 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post1)
                .content("comment_content1")
                .build());
        comment2 = commentRepository.save(Comment.builder()
                .user(user2)
                .post(post1)
                .content("comment_content2")
                .build());
        // 좋아요
        liking1 = likingRepository.save(Liking.builder()
                .post(post1)
                .user(user2)
                .build());

        post2 = postRepository.save(Post.builder()
                .user(user1)
                .category(CategoryType.TALK)
                .title("post_title2")
                .content("post_content2")
                .build());

        post3 = postRepository.save(Post.builder()
                .user(user2)
                .category(CategoryType.TALK)
                .title("post_title3")
                .content("post_content3")
                .build());
        // 좋아요
        liking2 = likingRepository.save(Liking.builder()
                .post(post3)
                .user(user1)
                .build());
    }

    @Test
    void get_paged_PostResponses_of_user() {

        // given
        // when
        Page<PostResponse> responses = postService.getPostResponsesOfUser(user1, 1);
        // then
        assertThat(responses.getContent())
                .hasSize(2)
                .contains(new PostResponse(post1), new PostResponse(post2))
                .doesNotContain(new PostResponse(post3));

        for (PostResponse response : responses) {

            if (response.getPostId().equals(post1.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post1.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user1.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user1.getImage());
                assertThat(response).extracting("category").isEqualTo(post1.getCategory());
                assertThat(response).extracting("title").isEqualTo(post1.getTitle());
                assertThat(response).extracting("content").isEqualTo(post1.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post1.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post1.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(1L);
                assertThat(response).extracting("commentCount").isEqualTo(2L);

            } else if (response.getPostId().equals(post2.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post2.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user1.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user1.getImage());
                assertThat(response).extracting("category").isEqualTo(post2.getCategory());
                assertThat(response).extracting("title").isEqualTo(post2.getTitle());
                assertThat(response).extracting("content").isEqualTo(post2.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post2.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post2.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(0L);
                assertThat(response).extracting("commentCount").isEqualTo(0L);

            } else {
                fail();
            }
        }
    }

    @Test
    void get_paged_PostResponses() {

        // given
        // when
        Page<PostResponse> responses = postService.getPostResponsesOfUser(user1, 1);
        // then
        assertThat(responses.getContent())
                .hasSize(3)
                .contains(new PostResponse(post1), new PostResponse(post2), new PostResponse(post3));

        for (PostResponse response : responses) {

            if (response.getPostId().equals(post1.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post1.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user1.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user1.getImage());
                assertThat(response).extracting("category").isEqualTo(post1.getCategory());
                assertThat(response).extracting("title").isEqualTo(post1.getTitle());
                assertThat(response).extracting("content").isEqualTo(post1.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post1.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post1.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(1L);
                assertThat(response).extracting("commentCount").isEqualTo(2L);

            } else if (response.getPostId().equals(post2.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post2.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user1.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user1.getImage());
                assertThat(response).extracting("category").isEqualTo(post2.getCategory());
                assertThat(response).extracting("title").isEqualTo(post2.getTitle());
                assertThat(response).extracting("content").isEqualTo(post2.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post2.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post2.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(0L);
                assertThat(response).extracting("commentCount").isEqualTo(0L);

            } else if (response.getPostId().equals(post3.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post3.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user2.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user2.getImage());
                assertThat(response).extracting("category").isEqualTo(post3.getCategory());
                assertThat(response).extracting("title").isEqualTo(post3.getTitle());
                assertThat(response).extracting("content").isEqualTo(post3.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post3.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post3.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(1L);
                assertThat(response).extracting("commentCount").isEqualTo(0L);

            } else {
                fail();
            }
        }
    }

    @Test
    void get_paged_PostResponses_when_searchText_is_blank() {

        // given
        // when
        Page<PostResponse> responses = postService.getPostResponses(user1, null, 1);
        // then
        assertThat(responses.getContent())
                .hasSize(3)
                .contains(new PostResponse(post1), new PostResponse(post2), new PostResponse(post3));

        for (PostResponse response : responses) {

            if (response.getPostId().equals(post1.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post1.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user1.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user1.getImage());
                assertThat(response).extracting("category").isEqualTo(post1.getCategory());
                assertThat(response).extracting("title").isEqualTo(post1.getTitle());
                assertThat(response).extracting("content").isEqualTo(post1.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post1.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post1.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(1L);
                assertThat(response).extracting("commentCount").isEqualTo(2L);

            } else if (response.getPostId().equals(post2.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post2.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user1.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user1.getImage());
                assertThat(response).extracting("category").isEqualTo(post2.getCategory());
                assertThat(response).extracting("title").isEqualTo(post2.getTitle());
                assertThat(response).extracting("content").isEqualTo(post2.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post2.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post2.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(0L);
                assertThat(response).extracting("commentCount").isEqualTo(0L);

            } else if (response.getPostId().equals(post3.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post3.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user2.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user2.getImage());
                assertThat(response).extracting("category").isEqualTo(post3.getCategory());
                assertThat(response).extracting("title").isEqualTo(post3.getTitle());
                assertThat(response).extracting("content").isEqualTo(post3.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post3.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post3.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(1L);
                assertThat(response).extracting("commentCount").isEqualTo(0L);

            } else {
                fail();
            }
        }
    }


    @Test
    void get_paged_PostResponses_when_searchText_is_not_blank() {

        // 제목/내용/댓글에 '2'를 포함하는 post
        // given
        // when
        Page<PostResponse> responses = postService.getPostResponses(user1, "2", 1);
        // then
        assertThat(responses.getContent())
                .hasSize(2)
                .contains(new PostResponse(post1), new PostResponse(post2));

        for (PostResponse response : responses) {

            if (response.getPostId().equals(post1.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post1.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user1.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user1.getImage());
                assertThat(response).extracting("category").isEqualTo(post1.getCategory());
                assertThat(response).extracting("title").isEqualTo(post1.getTitle());
                assertThat(response).extracting("content").isEqualTo(post1.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post1.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post1.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(1L);
                assertThat(response).extracting("commentCount").isEqualTo(2L);

            } else if (response.getPostId().equals(post2.getId())) {

                assertThat(response).extracting("postId").isEqualTo(post2.getId());
                assertThat(response).extracting("userNickname").isEqualTo(user1.getNickname());
                assertThat(response).extracting("userImage").isEqualTo(user1.getImage());
                assertThat(response).extracting("category").isEqualTo(post2.getCategory());
                assertThat(response).extracting("title").isEqualTo(post2.getTitle());
                assertThat(response).extracting("content").isEqualTo(post2.getContent());
                assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post2.getCreatedAt()));

                assertThat(response).extracting("hits").isEqualTo(post2.getHits());

                // setCounts
                assertThat(response).extracting("likingCount").isEqualTo(0L);
                assertThat(response).extracting("commentCount").isEqualTo(0L);

            } else {
                fail();
            }
        }
    }

    @Test
    void get_PostResponse() {

        // given
        // when
        PostResponse response = postService.getPostResponse(user1, post3.getId());
        // then

        assertAll(
                () -> assertThat(response).extracting("postId").isEqualTo(post3.getId()),
                () -> assertThat(response).extracting("userNickname").isEqualTo(user2.getNickname()),
                () -> assertThat(response).extracting("userImage").isEqualTo(user2.getImage()),
                () -> assertThat(response).extracting("category").isEqualTo(post3.getCategory()),
                () -> assertThat(response).extracting("title").isEqualTo(post3.getTitle()),
                () -> assertThat(response).extracting("content").isEqualTo(post3.getContent()),
                () -> assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post3.getCreatedAt())),

                () -> assertThat(response).extracting("hits").isEqualTo(post3.getHits()),

                // setCounts
                () -> assertThat(response).extracting("likingCount").isEqualTo(1L),
                () -> assertThat(response).extracting("commentCount").isEqualTo(0L)
        );

    }

    @DisplayName("댓글단 글 리스트 - 페이징")
    @Test
    void get_paged_commenting_PostResponses() {
        // user2가 댓글단 글 리스트

        // given
        // when
        Page<PostResponse> responses = postService.getCommentingPostResponses(user2, 1);
        // then
        assertThat(responses.getContent())
                .hasSize(1)
                .contains(new PostResponse(post1));
        PostResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response).extracting("postId").isEqualTo(post1.getId()),
                () -> assertThat(response).extracting("userNickname").isEqualTo(user1.getNickname()),
                () -> assertThat(response).extracting("userImage").isEqualTo(user1.getImage()),
                () -> assertThat(response).extracting("category").isEqualTo(post1.getCategory()),
                () -> assertThat(response).extracting("title").isEqualTo(post1.getTitle()),
                () -> assertThat(response).extracting("content").isEqualTo(post1.getContent()),
                () -> assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post1.getCreatedAt())),

                () -> assertThat(response).extracting("hits").isEqualTo(post1.getHits()),

                () -> assertThat(response).extracting("likingCount").isEqualTo(null),
                () -> assertThat(response).extracting("commentCount").isEqualTo(null)
        );
    }

    @DisplayName("좋아요한 글 리스트 - 페이징")
    @Test
    void get_paged_liking_PostResponses() {
        // user1이 좋아요한 글 리스트

        // given
        // when
        Page<PostResponse> responses = postService.getLikingPostResponses(user1, 1);
        // then
        assertThat(responses.getContent())
                .hasSize(1)
                .contains(new PostResponse(post3));
        PostResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response).extracting("postId").isEqualTo(post3.getId()),
                () -> assertThat(response).extracting("userNickname").isEqualTo(user2.getNickname()),
                () -> assertThat(response).extracting("userImage").isEqualTo(user2.getImage()),
                () -> assertThat(response).extracting("category").isEqualTo(post3.getCategory()),
                () -> assertThat(response).extracting("title").isEqualTo(post3.getTitle()),
                () -> assertThat(response).extracting("content").isEqualTo(post3.getContent()),
                () -> assertThat(response).extracting("createdAt").isEqualTo(LocalDateTimeUtil.getDateTimeToString(post3.getCreatedAt())),

                () -> assertThat(response).extracting("hits").isEqualTo(post3.getHits()),

                () -> assertThat(response).extracting("likingCount").isEqualTo(null),
                () -> assertThat(response).extracting("commentCount").isEqualTo(null)
        );
    }

    @Test
    void create_post() {

        // given
        // when
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("new_title")
                .content("new_content")
                .image("new_image")
                .build();
        Post saved = postService.createPost(user2, postCreateRequest);

        // then
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(4);
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getUser()).isEqualTo(user2),
                () -> assertThat(saved.getCategory()).isEqualTo(postCreateRequest.getCategory()),
                () -> assertThat(saved.getTitle()).isEqualTo(postCreateRequest.getTitle()),
                () -> assertThat(saved.getContent()).isEqualTo(postCreateRequest.getContent()),
                () -> assertThat(saved.getImage()).isEqualTo(postCreateRequest.getImage()),

                () -> assertThat(saved.getHits()).isEqualTo(0)
        );
    }

    @Test
    void update_not_existed_post() {

        // given
        // when
        // then
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .category(CategoryType.LECTURE_REQUEST)
                .title("updated_title")
                .content("updated_content")
                .image("updated_image")
                .build();
        assertThrows(EntityNotFoundException.class,
                () -> postService.updatePost(user1, 10000L, postUpdateRequest)
        );
    }

    @Test
    void update_post_not_by_postWriter() {

        // given
        // when
        // then
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .category(CategoryType.LECTURE_REQUEST)
                .title("updated_title")
                .content("updated_content")
                .image("updated_image")
                .build();
        assertThrows(UnauthorizedException.class,
                () -> postService.updatePost(user2, post1.getId(), postUpdateRequest)
        );
    }

    @Test
    void update_post() {
        // 댓글 달린 후에도 수정 가능

        // given
        // when
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .category(CategoryType.LECTURE_REQUEST)
                .title("updated_title")
                .content("updated_content")
                .image("updated_image")
                .build();
        postService.updatePost(user1, post1.getId(), postUpdateRequest);

        // then
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(3);
        assertAll(
                () -> assertThat(post1.getUser()).isEqualTo(user1),
                () -> assertThat(post1.getCategory()).isEqualTo(postUpdateRequest.getCategory()),
                () -> assertThat(post1.getTitle()).isEqualTo(postUpdateRequest.getTitle()),
                () -> assertThat(post1.getContent()).isEqualTo(postUpdateRequest.getContent()),
                () -> assertThat(post1.getImage()).isEqualTo(postUpdateRequest.getImage()),

                () -> assertThat(post1.getHits()).isEqualTo(0)
        );
    }

    @Test
    void delete_not_existed_post() {

        // given
        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> postService.deletePost(user1, 10000L)
        );
    }

    @Test
    void delete_post_by_not_postWriter() {

        // given
        // when
        // then
        assertThrows(UnauthorizedException.class,
                () -> postService.deletePost(user2, post1.getId())
        );
    }

    @Test
    void delete_post() {

        // given
        // when
        postService.deletePost(user1, post1.getId());

        // then
        List<Post> posts = postRepository.findAll();
        assertThat(posts).hasSize(2);
        assertThat(postRepository.findById(post1.getId()).isPresent()).isFalse();

        // 댓글 삭제
        assertThat(commentRepository.findById(comment1.getId()).isPresent()).isFalse();
        assertThat(commentRepository.findById(comment2.getId()).isPresent()).isFalse();
        // 좋아요 삭제
        assertThat(likingRepository.findById(liking1.getId()).isPresent()).isFalse();
    }

    @Test
    void like_post() {
        // user2 -> post2

        // given
        // when
        postService.likePost(user2, post2.getId());

        // then
        List<Liking> likings = likingRepository.findAll();
        assertThat(likings).hasSize(3);
        assertNotNull(likingRepository.findByUserAndPost(user2, post2));
    }

    @Test
    void cancel_like_post() {

        // given
        postService.likePost(user2, post2.getId());
        assertNotNull(likingRepository.findByUserAndPost(user2, post2));

        // when
        postService.likePost(user2, post2.getId());

        // then
        assertNull(likingRepository.findByUserAndPost(user2, post2));
    }

}