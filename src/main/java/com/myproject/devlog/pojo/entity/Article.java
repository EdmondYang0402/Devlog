package com.myproject.devlog.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章實體類
 */
@Data
public class Article {

    /**
     * 主鍵 ID (bigint, 自增)
     */
    private Long id;

    /**
     * 標題 (varchar)
     */
    private String title;

    /**
     * 摘要 (varchar)
     */
    private String summary;

    /**
     * 內容 (longtext)
     */
    private String content;

    /**
     * 封面圖片地址 (varchar)
     */
    private String coverImage;

    /**
     * 作者 ID (bigint)
     */
    private Long authorId;

    /**
     * 分類 ID (bigint)
     */
    private Long categoryId;

    /**
     * 狀態 (tinyint)
     * 例如：0-草稿，1-已發布
     */
    private Integer status;

    /**
     * 瀏覽量 (bigint)
     */
    private Long viewCount;

    /**
     * 創建時間 (datetime)
     */
    private LocalDateTime createTime;

    /**
     * 更新時間 (datetime)
     */
    private LocalDateTime updateTime;
}
