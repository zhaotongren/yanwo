package com.yanwo.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanwo.dao.SysleaveMessageDao;
import com.yanwo.entity.SysleaveMessageEntity;
import com.yanwo.service.SysleaveMessageService;
import org.springframework.stereotype.Service;


@Service("sysleaveMessageService")
public class SysleaveMessageServiceImpl extends ServiceImpl<SysleaveMessageDao, SysleaveMessageEntity> implements SysleaveMessageService {


}
