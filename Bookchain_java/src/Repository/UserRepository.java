package com.bookchain.repository;

import com.bookchain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口，继承自JpaRepository，提供对User实体的基本数据库操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名查找用户
    Optional<User> findByUsername(String username);

    // 根据电子邮箱查找用户
    Optional<User> findByEmail(String email);

    // 根据区块链地址查找用户
    Optional<User> findByBlockchainAddress(String blockchainAddress);
}
