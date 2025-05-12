package com.bookchain.service;

import com.bookchain.entity.User;
import com.bookchain.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户服务类，处理用户相关业务逻辑
 */
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 创建新用户（注册）
     */
    public User createUser(String email, String password, String realName, String interests) {
        // 校验邮箱唯一性
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        // 生成系统唯一用户名（如 USER_20231001_ABC）
        String username = generateSystemUsername();

        // 创建用户实体并加密密码
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // BCrypt加密
        user.setRealName(realName);
        user.setInterests(interests);
        user.setBlockchainAddress(generateBlockchainAddress()); // 模拟生成区块链地址

        return userRepository.save(user);
    }

    /**
     * 根据用户名查询用户
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据邮箱查询用户
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 更新用户兴趣标签
     */
    public void updateUserInterests(Long userId, String newInterests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setInterests(newInterests);
        userRepository.save(user);
    }

    /**
     * 生成系统唯一用户名（示例：USER_随机UUID片段）
     */
    private String generateSystemUsername() {
        return "USER_" + UUID.randomUUID().toString().toUpperCase().substring(0, 8);
    }

    /**
     * 模拟生成区块链地址（实际需对接区块链钱包）
     */
    private String generateBlockchainAddress() {
        return "0x" + UUID.randomUUID().toString().substring(0, 38);
    }
}
