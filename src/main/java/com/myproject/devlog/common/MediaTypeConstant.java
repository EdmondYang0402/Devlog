package com.myproject.devlog.common;

public final class MediaTypeConstant {
    // 一张表统一保存书籍、电影、番剧和游戏，类型码用于区分作品类别。
    public static final int BOOK = 0;
    public static final int MOVIE = 1;
    public static final int ANIME = 2;
    public static final int GAME = 3;

    private MediaTypeConstant() {
    }

    public static boolean isValid(Integer type) {
        return type != null && type >= BOOK && type <= GAME;
    }
}
