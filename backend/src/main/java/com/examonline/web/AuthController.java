package com.examonline.web;

import com.examonline.common.ApiResponse;
import com.examonline.domain.AppUser;
import com.examonline.service.UserService;
import com.examonline.web.dto.BasicDtos;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<AppUser> login(@RequestBody BasicDtos.LoginRequest request) {
        return ApiResponse.ok(userService.login(request.username(), request.password()));
    }

    @GetMapping("/me")
    public ApiResponse<AppUser> me(@RequestParam String username) {
        return ApiResponse.ok(userService.me(username));
    }

    @PutMapping("/password")
    public ApiResponse<AppUser> changePassword(@RequestBody BasicDtos.ChangePasswordRequest request) {
        return ApiResponse.ok(userService.changePassword(request.username(), request.oldPassword(), request.newPassword()));
    }
}
