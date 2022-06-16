package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.controller.request.UserImageUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserPasswordUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserQuitRequest;
import com.project.mentoridge.modules.account.controller.request.UserUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.UserResponse;
import com.project.mentoridge.modules.account.service.UserService;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"UserController"})
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // TODO - 검색
    @ApiOperation("회원 전체 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "1") Integer page) {

        Page<UserResponse> users = userService.getUserResponses(page);
        return ResponseEntity.ok(users);
    }

    @ApiOperation("회원 조회")
    @GetMapping("/{user_id}")
    public ResponseEntity<?> getUser(@PathVariable(name = "user_id") Long userId) {

        UserResponse user = userService.getUserResponse(userId);
        return ResponseEntity.ok(user);
    }

    @ApiOperation("내정보 조회")
    @GetMapping("/my-info")
    public ResponseEntity<?> getMyInfo(@CurrentUser User user) {
        return ResponseEntity.ok(userService.getUserResponse(user));
    }

    @ApiOperation("회원 정보 수정")
    @PutMapping("/my-info")
    public ResponseEntity<?> editUser(@CurrentUser User user,
                                      @Validated @RequestBody UserUpdateRequest userUpdateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        userService.updateUser(user, userUpdateRequest);
        return ok();
    }

    @ApiOperation("프로필 이미지 수정")
    @PutMapping("/my-info/image")
    public ResponseEntity<?> changeImage(@CurrentUser User user,
                                         @Validated @RequestBody UserImageUpdateRequest userImageUpdateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        userService.updateUserImage(user, userImageUpdateRequest);
        return ResponseEntity.ok().body(user.getImage());
    }

    @ApiOperation("회원 탈퇴")
    @DeleteMapping
    public ResponseEntity<?> quitUser(@CurrentUser User user,
                                      @Validated @RequestBody UserQuitRequest userQuitRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        userService.deleteUser(user, userQuitRequest);
        return ok();
    }

    @GetMapping("/quit-reasons")
    public Map<Integer, String> getQuitReasons() {
        return UserQuitRequest.reasons;
    }

    @ApiOperation("비밀번호 변경")
    @PutMapping("/my-password")
    public ResponseEntity<?> changeUserPassword(@CurrentUser User user,
                                                @Validated @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        userService.updateUserPassword(user, userPasswordUpdateRequest);
        return ok();
    }

}
