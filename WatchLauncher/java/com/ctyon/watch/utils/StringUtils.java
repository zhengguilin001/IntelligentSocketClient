package com.ctyon.watch.utils;

/**
 * Created by zx
 * On 2017/12/13
 */

public class StringUtils {
    /**
     * 判断字符串是否为 null 或长度为 0
     *
     * @param s 待校验字符串
     * @return {@code true}: 空<br> {@code false}: 不为空
     */
    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

}
