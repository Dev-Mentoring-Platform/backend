package com.project.mentoridge.modules.board.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"UserPostController"})
@RequestMapping("/api/users/my-posts")
@RestController
@RequiredArgsConstructor
public class UserPostController {

    private final PostService postService;

    @ApiOperation("작성한 글 리스트 - 페이징")
    @GetMapping
    public ResponseEntity<?> getPostsOfUser(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(postService.getPostResponsesOfUser(user, page));
    }

    @ApiOperation("글 조회")
    @GetMapping("/{post_id}")
    public ResponseEntity<?> getPost(@CurrentUser User user, @PathVariable(name = "post_id") Long postId) {
        return ResponseEntity.ok(postService.getPostResponse(user, postId));
    }

    @ApiOperation("글 수정")
    @PutMapping("/{post_id}")
    public ResponseEntity<?> editPost(@CurrentUser User user, @PathVariable(name = "post_id") Long postId,
                                      @Validated @RequestBody PostUpdateRequest updateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        postService.updatePost(user, postId, updateRequest);
        return ok();
    }

    @ApiOperation("글 삭제")
    @DeleteMapping("/{post_id}")
    public ResponseEntity<?> deletePost(@CurrentUser User user, @PathVariable(name = "post_id") Long postId) {
        postService.deletePost(user, postId);
        return ok();
    }

    @ApiOperation("댓글단 글 리스트 - 페이징")
    @GetMapping("/commenting")
    public ResponseEntity<?> getCommentingPosts(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(postService.getCommentingPostResponses(user, page));
    }

    @ApiOperation("좋아요한 글 리스트 - 페이징")
    @GetMapping("/liking")
    public ResponseEntity<?> getLikingPosts(@CurrentUser User user, @RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(postService.getLikingPostResponses(user, page));
    }
}
