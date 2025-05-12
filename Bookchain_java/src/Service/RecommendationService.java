package com.bookchain.service;

import com.bookchain.entity.Book;
import com.bookchain.entity.User;
import com.bookchain.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务类，实现书籍推荐算法
 */
@Service
public class RecommendationService {
    private final BookRepository bookRepository;

    public RecommendationService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * 根据用户兴趣推荐书籍（基于类别匹配）
     */
    public List<Book> recommendByInterests(User user, int limit) {
        if (user.getInterests() == null || user.getInterests().isEmpty()) {
            return Collections.emptyList(); // 用户无兴趣标签时不推荐
        }

        // 解析用户兴趣标签（如 "文学,科幻" 拆分为列表）
        List<String> interests = Arrays.asList(user.getInterests().split(","));

        // 查询所有非盲盒书籍中匹配兴趣类别的书籍
        List<Book> matchedBooks = bookRepository.findAll().stream()
                .filter(book -> !book.isBlindBox()) // 非盲盒书籍显示详情
                .filter(book -> interests.contains(book.getCategory()))
                .collect(Collectors.toList());

        // 随机排序并限制结果数量
        return matchedBooks.stream()
                .sorted((a, b) -> new Random().nextInt()) // 随机排序增加多样性
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 推荐热门盲盒书籍（基于交易频率）
     */
    public List<Book> recommendHotBlindBoxes(int limit) {
        // 查询所有盲盒书籍并按上传时间倒序（模拟热门程度，可替换为交易次数统计）
        return bookRepository.findByIsBlindBox(true).stream()
                .sorted((a, b) -> Long.compare(b.getUploadTime(), a.getUploadTime()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 推荐相似用户的书籍（简单协同过滤，示例逻辑）
     */
    public List<Book> recommendBySimilarUsers(User targetUser, int limit) {
        // 模拟逻辑：假设兴趣标签相同的用户为相似用户
        List<User> similarUsers = bookRepository.findAllUsersByInterests(targetUser.getInterests());
        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }

        // 收集相似用户的书籍并去重
        Set<Book> recommendedBooks = new HashSet<>();
        for (User user : similarUsers) {
            recommendedBooks.addAll(user.getOwnedBooks());
        }

        // 排除目标用户已拥有的书籍
        recommendedBooks.removeAll(targetUser.getOwnedBooks());

        return recommendedBooks.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}
