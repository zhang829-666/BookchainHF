
    package com.bookchain.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

    /**
     * Book实体类，用于表示图书信息
     */
    @Entity
    @Table(name = "bc_book")
    public class Book implements Serializable {
        private static final long serialVersionUID = 1L;

        // 书籍唯一标识，主键，使用自增长策略
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "book_id")
        private Long bookId;

        // 书籍标题
        @Column(name = "title", nullable = false, length = 255)
        private String title;

        // 书籍作者
        @Column(name = "author", length = 128)
        private String author;

        // 书籍描述
        @Column(name = "description", length = 1024)
        private String description;

        // 书籍类别，如"文学"、"科幻"等
        @Column(name = "category", length = 64)
        private String category;

        // 是否为盲盒书籍，true表示是盲盒，隐藏详细信息
        @Column(name = "is_blind_box", nullable = false)
        private boolean isBlindBox;

        // 书籍的区块链交易哈希，用于记录在区块链上的交易信息
        @Column(name = "blockchain_tx_hash", length = 64)
        private String blockchainTxHash;

        // 书籍上传时间
        @Column(name = "upload_time", nullable = false)
        private Date uploadTime = new Date();

        // 关联用户，多本书籍对应一个用户，即书籍所有者
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "owner_id")
        private User owner;

        // Getter和Setter方法
        public Long getBookId() {
            return bookId;
        }

        public void setBookId(Long bookId) {
            this.bookId = bookId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public boolean isBlindBox() {
            return isBlindBox;
        }

        public void setBlindBox(boolean blindBox) {
            isBlindBox = blindBox;
        }

        public String getBlockchainTxHash() {
            return blockchainTxHash;
        }

        public void setBlockchainTxHash(String blockchainTxHash) {
            this.blockchainTxHash = blockchainTxHash;
        }

        public Date getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(Date uploadTime) {
            this.uploadTime = uploadTime;
        }

        public User getOwner() {
            return owner;
        }

        public void setOwner(User owner) {
            this.owner = owner;
        }
    }

