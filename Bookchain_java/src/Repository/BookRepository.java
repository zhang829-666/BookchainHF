package com.bookchain.repository;

import com.bookchain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Book数据访问接口，继承自JpaRepository，用于对Book实体进行数据库操作
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // 根据书籍标题模糊查询书籍列表
    List<Book> findByTitleContaining(String title);

    // 根据作者查询书籍列表
    List<Book> findByAuthor(String author);

    // 根据是否为盲盒状态查询书籍列表
    List<Book> findByIsBlindBox(boolean isBlindBox);

    // 根据所有者用户ID查询其拥有的书籍列表
    List<Book> findByOwnerUserId(Long ownerUserId);

    // 根据区块链交易哈希查询书籍
    Optional<Book> findByBlockchainTxHash(String blockchainTxHash);
}
