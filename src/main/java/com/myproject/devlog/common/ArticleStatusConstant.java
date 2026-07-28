package com.myproject.devlog.common;

public final class ArticleStatusConstant {
    public static final int DRAFT_VALUE = 0;
    public static final int PUBLISHED_VALUE = 1;
    public static final Integer DRAFT = DRAFT_VALUE;
    public static final Integer PUBLISHED = PUBLISHED_VALUE;

    private ArticleStatusConstant() {
    }

    public static boolean isValid(Integer status) {
        return DRAFT.equals(status) || PUBLISHED.equals(status);
    }
}
