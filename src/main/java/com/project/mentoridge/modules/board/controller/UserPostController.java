package com.project.mentoridge.modules.board.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"UserPostController"})
@RequestMapping("/api/users/my-posts")
@RestController
@RequiredArgsConstructor
public class UserPostController {

    private final PostService postService;

    // 작성한 글 리스트 - 페이징

    // 글 조회

    // 글 수정
    @ApiOperation("글 수정")
    @PutMapping("/{post_id}")
    public ResponseEntity<?> updatePost(@CurrentUser User user, @PathVariable(name = "post_id") Long postId, @Valid @RequestBody PostUpdateRequest updateRequest) {
        postService.updatePost(user, postId, updateRequest);
        return ok();
    }

    // 글 삭제
    @ApiOperation("글 삭제")
    @DeleteMapping("/{post_id}")
    public ResponseEntity<?> deletePost(@CurrentUser User user, @PathVariable(name = "post_id") Long postId) {
        postService.deletePost(user, postId);
        return ok();
    }

    // 댓글단 글 리스트
    // 좋아요한 글 리스트
}
