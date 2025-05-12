package com.bookchain.repository;

import com.bookchain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 交易记录数据访问接口，继承自JpaRepository，用于对Transaction实体进行数据库操作
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 根据关联的书籍ID查询交易记录列表
    List<Transaction> findByBookBookId(Long bookId);

    // 根据交易发起方用户ID查询交易记录列表
    List<Transaction> findBySenderUserId(Long senderUserId);

    // 根据交易接收方用户ID查询交易记录列表
    List<Transaction> findByReceiverUserId(Long receiverUserId);

    // 根据交易类型查询交易记录列表
    List<Transaction> findByTransactionType(String transactionType);

    // 根据区块链交易哈希查询交易记录
    Optional<Transaction> findByBlockchainTxHash(String blockchainTxHash);
}