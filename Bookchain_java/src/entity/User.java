package com.bookchain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体类，对应数据库中的用户表
 * 包含用户基本信息、拥有的书籍列表、兴趣标签等
 */
@Entity
@Table(name = "bc_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L; // 序列化版本号

    // 主键：用户ID（自动生成）
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // 唯一用户名（系统自动生成的编号，如USER_20231001）
    @NotBlank(message = "用户名不能为空")
    @Column(name = "username", unique = true, nullable = false, length = 32)
    private String username;

    // 电子邮箱（用于注册和找回密码）
    @NotBlank(message = "邮箱不能为空")
    @Column(name = "email", unique = true, nullable = false, length = 128)
    private String email;

    // 加密后的登录密码
    @NotBlank(message = "密码不能为空")
    @Column(name = "password", nullable = false, length = 64) // 建议使用BCrypt加密存储
    private String password;

    // 用户真实姓名（可选）
    @Column(name = "real_name", length = 32)
    private String realName;

    // 用户兴趣标签（如 "文学,科幻,历史"）
    @Column(name = "interests", length = 256)
    private String interests;

    // 拥有的书籍列表（与Book实体类建立双向关联）
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> ownedBooks = new ArrayList<>();

    // 区块链地址（用于资产所有权标识）
    @Column(name = "blockchain_address", unique = true, length = 64)
    private String blockchainAddress;

    // 创建时间
    @Column(name = "create_time", nullable = false)
    private Long createTime = System.currentTimeMillis();

    // Getter 和 Setter 方法
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public List<Book> getOwnedBooks() { return ownedBooks; }
    public void setOwnedBooks(List<Book> ownedBooks) { this.ownedBooks = ownedBooks; }

    public String getBlockchainAddress() { return blockchainAddress; }
    public void setBlockchainAddress(String blockchainAddress) { this.blockchainAddress = blockchainAddress; }

    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }

    // 方便调试的toString方法（可选）
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", interests='" + interests + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}