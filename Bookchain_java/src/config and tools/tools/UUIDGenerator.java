package com.bookchain.utils;

import java.util.UUID;

/**
 * UUID生成工具类，提供标准化唯一标识符生成方法
 */
public class UUIDGenerator {

    /**
     * 生成32位全小写UUID（去除连字符）
     */
    public static String generate32UUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 生成指定长度的UUID片段（从32位UUID中截取前length位）
     * @param length 目标长度（建议≤32）
     * @throws IllegalArgumentException 长度超过32时抛出异常
     */
    public static String generateShortUUID(int length) {
        if (length > 32) {
            throw new IllegalArgumentException("UUID片段长度不能超过32位");
        }
        String fullUUID = generate32UUID();
        return fullUUID.substring(0, length);
    }

    /**
     * 生成带前缀的唯一编号（如 "USER_202310_ABC123"）
     * @param prefix 自定义前缀（如 "ORDER_"）
     * @param suffixLength 后缀长度（建议4-8位）
     */
    public static String generatePrefixedUUID(String prefix, int suffixLength) {
        if (suffixLength < 4 || suffixLength > 8) {
            throw new IllegalArgumentException("后缀长度需在4-8位之间");
        }
        String suffix = generateShortUUID(suffixLength);
        return prefix + "_" + suffix.toUpperCase();
    }

    // 示例用法
    public static void main(String[] args) {
        System.out.println("32位UUID: " + generate32UUID()); // e.g. b9d34c5f2a1b34f88b4c6d7e8f9a0b1c
        System.out.println("8位片段: " + generateShortUUID(8)); // e.g. b9d34c5f
        System.out.println("带前缀编号: " + generatePrefixedUUID("USER", 6)); // e.g. USER_2A1B3C
    }
}
