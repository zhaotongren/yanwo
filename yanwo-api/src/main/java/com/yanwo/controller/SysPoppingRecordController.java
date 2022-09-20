package com.yanwo.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanwo.entity.SysPoppingEntity;
import com.yanwo.entity.UserEntity;
import com.yanwo.service.SysPoppingService;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.PageUtils;
import com.yanwo.utils.R;
import com.yanwo.validator.ValidatorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yanwo.entity.SysPoppingRecordEntity;
import com.yanwo.service.SysPoppingRecordService;



/**
 * 弹窗记录
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2020-06-08 11:06:51
 */
@RestController
@RequestMapping("yanwo/syspoppingrecord")
public class SysPoppingRecordController extends BaseController{
    @Autowired
    private SysPoppingRecordService sysPoppingRecordService;
    @Autowired
    private SysPoppingService sysPoppingService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sysPoppingRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info")
    public R info(@RequestHeader String token){
        QueryWrapper<SysPoppingEntity> sysPoppingEntityQueryWrapper = new QueryWrapper<>();
        sysPoppingEntityQueryWrapper.eq("status",0);
        SysPoppingEntity poppingServiceOne = sysPoppingService.getOne(sysPoppingEntityQueryWrapper);
        if (poppingServiceOne==null){
            return R.ok();
        }
        if (poppingServiceOne.getExpiresTime()!=0){
            if (poppingServiceOne.getExpiresTime()< DateUtils.currentUnixTime()){
                poppingServiceOne.setStatus(1);
                sysPoppingService.updateById(poppingServiceOne);
                return R.ok();
            }
        }
        UserEntity userEntity = getUserFromTokenApp(token);
        if (userEntity==null){
            return R.ok().put("popping",poppingServiceOne);
        }else {
            QueryWrapper<SysPoppingRecordEntity> recordEntityQueryWrapper = new QueryWrapper<>();
            recordEntityQueryWrapper.eq("user_id",userEntity.getUserId());
            SysPoppingRecordEntity sysPoppingRecord = sysPoppingRecordService.getOne(recordEntityQueryWrapper);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDay = dateFormat.format(new Date());
            Integer agoTime = DateUtils.currentUnixTime();
            if (sysPoppingRecord == null){
                SysPoppingRecordEntity poppingRecordEntity = new SysPoppingRecordEntity();
                poppingRecordEntity.setUserId(userEntity.getUserId());
                poppingRecordEntity.setCurrentData(currentDay);
                poppingRecordEntity.setPoppingId(poppingServiceOne.getPoppingId());
                poppingRecordEntity.setDayCount(1);
                poppingRecordEntity.setTotal(1);
                poppingRecordEntity.setAgoTime(agoTime);
                sysPoppingRecordService.save(poppingRecordEntity);
                return R.ok().put("popping",poppingServiceOne);
            }else {
                if (!sysPoppingRecord.getPoppingId().equals(poppingServiceOne.getPoppingId())){
                    //如果弹窗已更改，则重置记录
                    sysPoppingRecord.setPoppingId(poppingServiceOne.getPoppingId());
                    sysPoppingRecord.setTotal(1);
                    sysPoppingRecord.setDayCount(1);
                    sysPoppingRecord.setCurrentData(currentDay);
                    sysPoppingRecord.setAgoTime(agoTime);
                    sysPoppingRecordService.updateById(sysPoppingRecord);
                    return R.ok().put("popping",poppingServiceOne);
                }else {

                    if (poppingServiceOne.getIntervalMin()!=0){
                        //还未超过间隔时间，则不弹出
                        boolean isIntervalMin = poppingServiceOne.getIntervalMin()+60+sysPoppingRecord.getAgoTime()>=DateUtils.currentUnixTime();
                        if (isIntervalMin){
                            return R.ok();
                        }
                    }
                    if (currentDay.equals(sysPoppingRecord.getCurrentData())) {
                        //同一天的弹窗记录
                        if (poppingServiceOne.getPoppingSum()==0){
                            if (poppingServiceOne.getPoppingDayCount()==0){
                                return R.ok().put("popping",poppingServiceOne);
                            }else {
                                if (sysPoppingRecord.getDayCount()>=poppingServiceOne.getPoppingDayCount()){
                                    return R.ok();
                                }else {
                                    sysPoppingRecord.setDayCount(sysPoppingRecord.getDayCount()+1);
                                    sysPoppingRecord.setTotal(sysPoppingRecord.getTotal()+1);
                                    sysPoppingRecord.setAgoTime(agoTime);
                                    sysPoppingRecordService.updateById(sysPoppingRecord);
                                    return R.ok().put("popping",poppingServiceOne);
                                }
                            }
                        }else {
                            if (sysPoppingRecord.getTotal()>=poppingServiceOne.getPoppingSum()){
                                return R.ok();
                            }else {
                                if (poppingServiceOne.getPoppingDayCount()==0){
                                    sysPoppingRecord.setDayCount(sysPoppingRecord.getDayCount()+1);
                                    sysPoppingRecord.setTotal(sysPoppingRecord.getTotal()+1);
                                    sysPoppingRecord.setAgoTime(agoTime);
                                    sysPoppingRecordService.updateById(sysPoppingRecord);
                                    return R.ok().put("popping",poppingServiceOne);
                                }else {
                                    if (sysPoppingRecord.getDayCount()>=poppingServiceOne.getPoppingDayCount()){
                                        return R.ok();
                                    }else {
                                        sysPoppingRecord.setDayCount(sysPoppingRecord.getDayCount()+1);
                                        sysPoppingRecord.setTotal(sysPoppingRecord.getTotal()+1);
                                        sysPoppingRecord.setAgoTime(agoTime);
                                        sysPoppingRecordService.updateById(sysPoppingRecord);
                                        return R.ok().put("popping",poppingServiceOne);
                                    }
                                }
                            }
                        }
                    }else {
                        //不是同一天的记录
                        if (poppingServiceOne.getPoppingSum()==0){
                            sysPoppingRecord.setDayCount(1);
                            sysPoppingRecord.setTotal(sysPoppingRecord.getTotal()+1);
                            sysPoppingRecord.setCurrentData(currentDay);
                            sysPoppingRecord.setAgoTime(agoTime);
                            sysPoppingRecordService.updateById(sysPoppingRecord);
                            return R.ok().put("popping",poppingServiceOne);
                        }else {
                            if (sysPoppingRecord.getTotal()>=poppingServiceOne.getPoppingSum()){
                                return R.ok();
                            }else {
                                sysPoppingRecord.setDayCount(1);
                                sysPoppingRecord.setTotal(sysPoppingRecord.getTotal()+1);
                                sysPoppingRecord.setCurrentData(currentDay);
                                sysPoppingRecordService.updateById(sysPoppingRecord);
                                sysPoppingRecord.setAgoTime(agoTime);
                                return R.ok().put("popping",poppingServiceOne);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SysPoppingRecordEntity sysPoppingRecord){
        sysPoppingRecordService.save(sysPoppingRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SysPoppingRecordEntity sysPoppingRecord){
        ValidatorUtils.validateEntity(sysPoppingRecord);
        sysPoppingRecordService.updateById(sysPoppingRecord);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] userIds){
        sysPoppingRecordService.removeByIds(Arrays.asList(userIds));

        return R.ok();
    }

}
