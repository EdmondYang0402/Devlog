package com.myproject.devlog.common;

public final class ProjectStatusConstant {
    /** 尚处于方案整理或等待启动阶段。 */
    public static final int PLANNING = 0;
    /** 已经开始编码、验证或迭代。 */
    public static final int IN_PROGRESS = 1;
    /** 既定目标已经完成。 */
    public static final int COMPLETED = 2;
    /** 主体已可用，但仍会持续维护。 */
    public static final int MAINTAINING = 3;
    /** 已停止迭代，仅保留历史展示。 */
    public static final int ARCHIVED = 4;

    private ProjectStatusConstant() {
    }

    public static boolean isValid(Integer status) {
        return status != null && status >= PLANNING && status <= ARCHIVED;
    }
}
