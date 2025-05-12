package com.bookchain.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 交易记录实体类，用于记录书籍在平台上的交易信息
 */
@Entity
@Table(name = "bc_transaction")
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    // 交易记录唯一标识，主键，使用自增长策略
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    // 关联的书籍ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    // 交易发起方用户ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id")
    private User sender;

    // 交易接收方用户ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    private User receiver;

    // 交易类型，如"交换"、"购买"、"赠送"等
    @Column(name = "transaction_type", length = 32, nullable = false)
    private String transactionType;

    // 交易发生时间
    @Column(name = "transaction_time", nullable = false)
    private Date transactionTime = new Date();

    // 区块链上对应的交易哈希值，用于验证交易真实性和追溯
    @Column(name = "blockchain_tx_hash", length = 64)
    private String blockchainTxHash;

    // 交易备注信息（可选）
    @Column(name = "remark", length = 256)
    private String remark;

    // Getter和Setter方法
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getBlockchainTxHash() {
        return blockchainTxHash;
    }

    public void setBlockchainTxHash(String blockchainTxHash) {
        this.blockchainTxHash = blockchainTxHash;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
