package com.yanwo.modules.service.impl;

import com.yanwo.dao.SysleaveMessageDao;
import com.yanwo.dao.UserDao;
import com.yanwo.entity.SysleaveMessageEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.modules.service.SysleaveMessageService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.common.utils.Query;

@Service("sysleaveMessageService")
public class SysleaveMessageServiceImpl extends ServiceImpl<SysleaveMessageDao, SysleaveMessageEntity> implements SysleaveMessageService {
    public static Logger logger = LoggerFactory.getLogger(SysleaveMessageServiceImpl.class);
    @Autowired
    private UserDao userDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysleaveMessageEntity> page = this.page(
                new Query<SysleaveMessageEntity>().getPage(params),
                new QueryWrapper<SysleaveMessageEntity>()
        );
        List<SysleaveMessageEntity> list = page.getRecords();
        for(SysleaveMessageEntity message:list){
            UserEntity userEntity = userDao.selectById(message.getUserId());
            if(StringUtils.isNotBlank(userEntity.getNickName())){
                message.setUserName(userEntity.getNickName());
            }else if(StringUtils.isNotBlank(userEntity.getMobile())){
                message.setUserName(userEntity.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
            }
            //留言内容转码(有表情)
            try {
                message.setLeaveMessage(URLDecoder.decode(message.getLeaveMessage(), "utf-8"));
            }catch (Exception e){
                logger.info("查询留言列表异常：",e);
            }
            message.setDateStr(DateUtils.getDateStrByTimestamp(message.getCreateTime(),DateUtils.DATE_TIME_PATTERN));

        }

        return new PageUtils(page);
    }

}
