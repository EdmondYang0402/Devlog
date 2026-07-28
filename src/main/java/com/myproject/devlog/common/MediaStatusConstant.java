package com.myproject.devlog.common;

public final class MediaStatusConstant {
    // 四类作品共享“计划、进行中、完成、搁置”这组状态，具体展示文案由前端按类型转换。
    public static final int PLANNED = 0;
    public static final int IN_PROGRESS = 1;
    public static final int COMPLETED = 2;
    public static final int DROPPED = 3;

    private MediaStatusConstant() {
    }

    public static boolean isValid(Integer status) {
        return status != null && status >= PLANNED && status <= DROPPED;
    }
}
