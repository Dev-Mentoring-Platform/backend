package com.project.mentoridge.modules.board.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.controller.request.CommentUpdateRequest;
import com.project.mentoridge.modules.board.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;
import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"CommentController"})
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ApiOperation("댓글 리스트 - 페이징")
    @GetMapping("/{post_id}/comments")
    public ResponseEntity<?> getComments(@CurrentUser User user, @PathVariable(name = "post_id") Long postId, @RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(commentService.getCommentResponses(user, postId, page));
    }

    @ApiOperation("댓글 작성")
    @PostMapping("/{post_id}/comments")
    public ResponseEntity<?> newComment(@CurrentUser User user, @PathVariable(name = "post_id") Long postId,
                                        @Validated @RequestBody CommentCreateRequest createRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        commentService.createComment(user, postId, createRequest);
        return created();
    }

    @ApiOperation("댓글 수정")
    @PutMapping("/{post_id}/comments/{comment_id}")
    public ResponseEntity<?> editComment(@CurrentUser User user, @PathVariable(name = "post_id") Long postId,
                                         @PathVariable(name = "comment_id") Long commentId,
                                         @Validated @RequestBody CommentUpdateRequest updateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        commentService.updateComment(user, postId, commentId, updateRequest);
        return ok();
    }

    @ApiOperation("댓글 삭제")
    @DeleteMapping("/{post_id}/comments/{comment_id}")
    public ResponseEntity<?> deleteComment(@CurrentUser User user, @PathVariable(name = "post_id") Long postId,
                                           @PathVariable(name = "comment_id") Long commentId) {
        commentService.deleteComment(user, postId, commentId);
        return ok();
    }
}
