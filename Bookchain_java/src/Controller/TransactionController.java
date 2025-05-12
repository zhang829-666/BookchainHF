package com.bookchain.controller;

import com.bookchain.entity.Transaction;
import com.bookchain.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 交易记录控制器类，处理交易相关的HTTP请求
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 查询所有交易记录（管理员权限示例）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 仅管理员可访问
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    /**
     * 根据书籍ID查询交易记录
     */
    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasRole('USER')")
    public List<Transaction> getTransactionsByBookId(@PathVariable Long bookId) {
        return transactionService.getTransactionsByBookId(bookId);
    }

    /**
     * 根据用户ID查询交易记录（发送方或接收方）
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public List<Transaction> getTransactionsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) String type // type=send或receive
    ) {
        if ("send".equalsIgnoreCase(type)) {
            return transactionService.getTransactionsBySender(userId);
        } else if ("receive".equalsIgnoreCase(type)) {
            return transactionService.getTransactionsByReceiver(userId);
        }
        return transactionService.getTransactionsByUser(userId);
    }

    /**
     * 根据交易类型过滤记录（如购买、交换）
     */
    @GetMapping("/filter")
    @PreAuthorize("hasRole('USER')")
    public List<Transaction> filterTransactionsByType(
            @RequestParam String transactionType
    ) {
        return transactionService.filterByTransactionType(transactionType);
    }

    /**
     * 查询单个交易记录（通过区块链哈希）
     */
    @GetMapping("/{txHash}")
    @PreAuthorize("hasRole('USER')")
    public Transaction getTransactionByHash(@PathVariable String txHash) {
        return transactionService.getTransactionByBlockchainTxHash(txHash)
                .orElseThrow(() -> new IllegalArgumentException("交易记录不存在"));
    }
}
