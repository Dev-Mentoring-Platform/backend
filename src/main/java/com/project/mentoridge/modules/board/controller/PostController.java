package com.project.mentoridge.modules.board.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.config.security.Nullable;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.controller.request.ContentSearchRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"PostController"})
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @ApiOperation("글 카테고리 목록")
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(CategoryType.values());
    }

    // TODO - 글 검색 with Elastic Search
    @ApiOperation("글 리스트 - 페이징")
    @GetMapping
    public ResponseEntity<?> getPosts(@CurrentUser User user, @RequestParam(name = "search", required = false) String search, @RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(postService.getPostResponses(user, search, page));
    }

    @ApiOperation("글 조회")
    @GetMapping("/{post_id}")
    public ResponseEntity<?> getPost(@CurrentUser User user, @PathVariable(name = "post_id") Long postId) {
        return ResponseEntity.ok(postService.getPostResponse(user, postId));
    }

    @ApiOperation("글 작성")
    @PostMapping
    public ResponseEntity<?> newPost(@CurrentUser User user, @Valid @RequestBody PostCreateRequest createRequest) {
        postService.createPost(user, createRequest);
        return created();
    }

    @ApiOperation("글 좋아요 / 좋아요 취소")
    @PostMapping("/{post_id}/like")
    public ResponseEntity<?> likePost(@CurrentUser User user, @PathVariable(name = "post_id") Long postId) {
        postService.likePost(user, postId);
        return ok();
    }
}
