package com.project.mentoridge.modules.board.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.project.mentoridge.config.response.Response.created;

@Api(tags = {"PostController"})
@RequestMapping("/api/posts")
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // TODO - 글 검색
    // 글 리스트 - 페이징
    // 글 조회 + 댓글 리스트

    // 글 작성
    @ApiOperation("글 작성")
    @PostMapping
    public ResponseEntity<?> newPost(@CurrentUser User user, @Valid @RequestBody PostCreateRequest createRequest) {
        postService.createPost(user, createRequest);
        return created();
    }

    // 글 좋아요 / 좋아요 취소

}
