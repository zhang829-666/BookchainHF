package com.bookchain.controller;

import com.bookchain.entity.User;
import com.bookchain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器类，处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口（公开访问）
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        String password = requestBody.get("password");
        String realName = requestBody.get("realName", "");
        String interests = requestBody.get("interests", "");

        return userService.createUser(email, password, realName, interests);
    }

    /**
     * 用户登录接口（示例，实际需结合Spring Security）
     */
    @PostMapping("/login")
    public String loginUser(@RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        // 示例逻辑：实际应通过Spring Security处理认证
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        // 校验密码（需在UserService中实现匹配逻辑）
        // if (userService.authenticateUser(username, password)) { ... }
        return "登录成功（示例返回，实际应返回JWT令牌）";
    }

    /**
     * 获取当前用户信息（需认证）
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')") // 权限控制示例
    public User getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        return userService.getUserByUsername(userId.toString())
                .orElseThrow(() -> new IllegalArgumentException("用户未登录"));
    }

    /**
     * 更新用户兴趣标签（需认证）
     */
    @PutMapping("/interests")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateInterests(@RequestHeader("X-User-Id") Long userId,
                                @RequestBody String newInterests) {
        userService.updateUserInterests(userId, newInterests);
    }

    /**
     * 根据用户名查询用户信息（公开访问，示例）
     */
    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }
}
