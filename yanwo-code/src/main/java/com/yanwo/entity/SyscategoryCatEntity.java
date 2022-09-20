package com.yanwo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("syscategory_cat")
public class SyscategoryCatEntity {
    /**
     * 分类ID
     */
    @TableId
    private Integer catId;
    /**
     * 分类父级ID
     */
    private Integer parentId;
    /**
     * 分类名称
     */
    private String catName;
    /**
     * 分类logo
     */
    private String catLogo;
    /**
     * 分类级别
     */
    private String level;
    /**
     * 是否屏蔽（true：是；false：否）
     */
    private Boolean disabled;
    /**
     * 排序
     */
    private Integer orderSort;

}