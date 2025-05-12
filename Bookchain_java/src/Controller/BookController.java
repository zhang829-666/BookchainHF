package com.bookchain.controller;

import com.bookchain.entity.Book;
import com.bookchain.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书籍控制器类，处理书籍相关的HTTP请求
 */
@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 上传普通书籍（需认证）
     */
    @PostMapping("/normal")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Book uploadNormalBook(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody BookRequest request
    ) {
        return bookService.uploadNormalBook(userId,
                request.title(), request.author(),
                request.description(), request.category());
    }

    /**
     * 创建盲盒书籍（需认证）
     */
    @PostMapping("/blind-box")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBlindBox(@RequestHeader("X-User-Id") Long userId) {
        return bookService.createBlindBox(userId);
    }

    /**
     * 查询所有书籍（可过滤盲盒）
     */
    @GetMapping
    public List<Book> getAllBooks(@RequestParam(required = false, defaultValue = "false") boolean includeBlindBox) {
        return bookService.getAllBooks(includeBlindBox);
    }

    /**
     * 根据ID查询书籍（处理盲盒可见性）
     */
    @GetMapping("/{bookId}")
    public Book getBookById(
            @PathVariable Long bookId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "false") boolean isOwner
    ) {
        // 校验是否为所有者（简化逻辑，实际需对比用户ID）
        boolean isActualOwner = userId.equals(bookService.getBookById(bookId, isOwner).getOwner().getUserId());
        return bookService.getBookById(bookId, isActualOwner);
    }

    /**
     * 转移书籍所有权（需认证）
     */
    @PutMapping("/{bookId}/transfer")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transferOwnership(
            @PathVariable Long bookId,
            @RequestHeader("X-User-Id") Long currentUserId,
            @RequestBody TransferRequest request
    ) {
        bookService.transferOwnership(bookId, request.newOwnerUserId());
    }

    /**
     * 请求体类：普通书籍上传参数
     */
    public record BookRequest(
            String title,
            String author,
            String description,
            String category
    ) {}

    /**
     * 请求体类：所有权转移参数
     */
    public record TransferRequest(
            Long newOwnerUserId
    ) {}
}
