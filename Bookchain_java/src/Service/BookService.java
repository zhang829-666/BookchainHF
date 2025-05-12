package com.bookchain.service;

import com.bookchain.entity.Book;
import com.bookchain.entity.User;
import com.bookchain.repository.BookRepository;
import com.bookchain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 书籍服务类，处理书籍上传、盲盒创建、所有权转移等业务逻辑
 */
@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BlockchainService blockchainService; // 假设存在区块链交互服务

    public BookService(BookRepository bookRepository,
                       UserRepository userRepository,
                       BlockchainService blockchainService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.blockchainService = blockchainService;
    }

    /**
     * 上传普通书籍（非盲盒）
     */
    public Book uploadNormalBook(Long userId, String title, String author,
                                 String description, String category) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setCategory(category);
        book.setIsBlindBox(false);
        book.setOwner(owner); // 关联所有者

        // 保存到数据库并同步区块链（示例逻辑）
        Book savedBook = bookRepository.save(book);
        blockchainService.recordBookOwnership(savedBook); // 调用区块链服务存证
        return savedBook;
    }

    /**
     * 创建盲盒书籍（隐藏详情）
     */
    public Book createBlindBox(Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        Book blindBox = new Book();
        blindBox.setTitle("神秘盲盒-" + System.currentTimeMillis());
        blindBox.setDescription("打开后可见书籍详情");
        blindBox.setIsBlindBox(true);
        blindBox.setOwner(owner);

        // 生成随机类别（示例逻辑，可替换为用户选择）
        blindBox.setCategory(getRandomCategory());

        Book savedBox = bookRepository.save(blindBox);
        blockchainService.createBlindBoxOnChain(savedBox); // 区块链创建盲盒
        return savedBox;
    }

    /**
     * 查询所有书籍（可过滤盲盒状态）
     */
    public List<Book> getAllBooks(boolean includeBlindBox) {
        if (includeBlindBox) {
            return bookRepository.findAll();
        }
        return bookRepository.findByIsBlindBox(false);
    }

    /**
     * 根据ID查询书籍（处理盲盒详情可见逻辑）
     */
    public Book getBookById(Long bookId, boolean isOwner) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("书籍不存在"));

        // 非所有者且为盲盒时，隐藏详细信息
        if (!isOwner && book.isBlindBox()) {
            book.setDescription("盲盒未打开，暂不可见");
            book.setAuthor(null);
            book.setCategory("神秘类别");
        }
        return book;
    }

    /**
     * 转移书籍所有权（交易核心逻辑）
     */
    public void transferOwnership(Long bookId, Long newOwnerId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("书籍不存在"));
        User newOwner = userRepository.findById(newOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("新所有者不存在"));

        // 校验当前所有者是否允许转移（示例逻辑，可扩展业务规则）
        if (!book.getOwner().getUserId().equals(book.getOwner().getUserId())) {
            throw new IllegalArgumentException("无权限转移他人书籍");
        }

        // 更新数据库和区块链
        book.setOwner(newOwner);
        bookRepository.save(book);
        blockchainService.transferOwnershipOnChain(book); // 区块链记录转移
    }
    private String getRandomCategory() {
        String[] categories = {"文学", "科幻", "历史", "技术", "艺术"};
        int randomIndex = (int) (Math.random() * categories.length);
        return categories[randomIndex];
    }
}
