package com.bookchain.service;

import com.bookchain.entity.Book;
import com.bookchain.entity.Transaction;
import com.bookchain.entity.User;
import com.bookchain.repository.TransactionRepository;
import org.hyperledger.fabric.gateway.*;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * 区块链服务类，负责与Hyperledger Fabric网络交互
 */
@Service
public class BlockchainService {
    private final TransactionRepository transactionRepository;
    private final Gateway gateway; // 区块链网关连接（需从配置获取）

    public BlockchainService(TransactionRepository transactionRepository, Gateway gateway) {
        this.transactionRepository = transactionRepository;
        this.gateway = gateway;
    }

    /**
     * 记录书籍所有权上链
     */
    public void recordBookOwnership(Book book) {
        try (Contract contract = gateway.getContract("bookchain")) { // 链码名称
            // 构造上链数据：书籍ID、所有者地址、类型（普通书籍）
            byte[] result = contract.submitTransaction(
                    "createAsset",
                    book.getBookId().toString(),
                    book.getOwner().getBlockchainAddress(),
                    "NORMAL",
                    Instant.now().toString()
            );
            String txHash = new String(result, StandardCharsets.UTF_8);
            book.setBlockchainTxHash(txHash); // 保存哈希到数据库
        } catch (Exception e) {
            throw new RuntimeException("区块链记录失败", e);
        }
    }

    /**
     * 创建盲盒资产上链
     */
    public void createBlindBoxOnChain(Book blindBox) {
        try (Contract contract = gateway.getContract("bookchain")) {
            // 构造上链数据：盲盒ID、所有者地址、类型（BLIND_BOX）
            byte[] result = contract.submitTransaction(
                    "createAsset",
                    blindBox.getBookId().toString(),
                    blindBox.getOwner().getBlockchainAddress(),
                    "BLIND_BOX",
                    Instant.now().toString()
            );
            String txHash = new String(result, StandardCharsets.UTF_8);
            blindBox.setBlockchainTxHash(txHash);

            // 记录交易到数据库
            Transaction transaction = new Transaction();
            transaction.setBook(blindBox);
            transaction.setTransactionType("CREATE_BLIND_BOX");
            transaction.setBlockchainTxHash(txHash);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            throw new RuntimeException("盲盒上链失败", e);
        }
    }

    /**
     * 转移所有权上链
     */
    public void transferOwnershipOnChain(Book book) {
        try (Contract contract = gateway.getContract("bookchain")) {
            // 调用链码转移所有权（参数：资产ID、新所有者地址）
            byte[] result = contract.submitTransaction(
                    "transferAsset",
                    book.getBookId().toString(),
                    book.getOwner().getBlockchainAddress()
            );
            String txHash = new String(result, StandardCharsets.UTF_8);

            // 记录交易到数据库
            Transaction transaction = new Transaction();
            transaction.setBook(book);
            transaction.setSender(book.getOwner()); // 原所有者
            transaction.setReceiver(book.getOwner()); // 新所有者（需从业务层获取）
            transaction.setTransactionType("TRANSFER");
            transaction.setBlockchainTxHash(txHash);
            transactionRepository.save(transaction);
        } catch (Exception e) {
            throw new RuntimeException("所有权转移失败", e);
        }
    }

    /**
     * 查询区块链资产状态（示例）
     */
    public String queryAsset(String assetId) {
        try (Contract contract = gateway.getContract("bookchain")) {
            byte[] result = contract.evaluateTransaction("readAsset", assetId);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("资产查询失败", e);
        }
    }
}
