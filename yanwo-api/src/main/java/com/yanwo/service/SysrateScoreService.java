package com.yanwo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanwo.entity.SysrateScoreEntity;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-04-16 10:05:42
 */
public interface SysrateScoreService extends IService<SysrateScoreEntity> {

    PageUtils queryPage(Map<String, Object> params);
    R tradeEvaluate(SysrateScoreEntity sysrateScore, String[] oids, String[] results, String[] contents, List<String> imgs);
}

