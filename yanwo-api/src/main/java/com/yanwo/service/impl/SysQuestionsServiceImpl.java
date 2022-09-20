package com.yanwo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysQuestionsDao;
import com.yanwo.entity.SysQuestionsEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.SysQuestionsService;
import com.yanwo.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

@Service("sysQuestionsService")
public class SysQuestionsServiceImpl extends ServiceImpl<SysQuestionsDao, SysQuestionsEntity> implements SysQuestionsService {


    @Override
    public PageUtils queryPage(QueryWrapper<SysQuestionsEntity> queryWrapper, Integer currPage) {
        Page<SysQuestionsEntity> page = new Page();
        page.setSize(3);
        page.setCurrent(currPage);
        IPage iPage =page(page, queryWrapper);
        iPage.setRecords(convertList(iPage.getRecords()));
        return new PageUtils(iPage);
    }

    public List convertList(List<SysQuestionsEntity>  questionsList) {
        List<ModelMap> list = new ArrayList<ModelMap>();
        if ( questionsList != null &&  questionsList.size() > 0) {
            for (SysQuestionsEntity  questions : questionsList) {
                ModelMap map = buildModelList(questions);
                list.add(map);
            }
        }
        return list;
    }

    public ModelMap buildModelList(SysQuestionsEntity  questions) {
        ModelMap model = new ModelMap();
        model.put("questionsId",questions.getQuestionsId());
        model.put("issue",questions.getIssue());
        model.put("answer",questions.getAnswer());

        return model;
    }
}
