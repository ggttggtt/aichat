package com.example.springboot.service;

import com.example.springboot.entity.Moment;

import java.util.List;

/**
 * 用户动态服务接口
 */
public interface MomentService {
    
    /**
     * 获取动态列表
     * @param userId 当前用户ID
     * @param page 页码
     * @param size 每页条数
     * @return 动态列表
     */
    List<Moment> listMoments(Integer userId, Integer page, Integer size);
    
    /**
     * 获取好友的动态列表
     * @param userId 当前用户ID
     * @param page 页码
     * @param size 每页条数
     * @return 动态列表
     */
    List<Moment> listFriendMoments(Integer userId, Integer page, Integer size);
    
    /**
     * 获取用户的动态列表
     * @param userId 用户ID
     * @param visitorId 访问者ID
     * @param page 页码
     * @param size 每页条数
     * @return 动态列表
     */
    List<Moment> listUserMoments(Integer userId, Integer visitorId, Integer page, Integer size);
    
    /**
     * 根据ID获取动态
     * @param id 动态ID
     * @param userId 当前用户ID
     * @return 动态信息
     */
    Moment getMomentById(Integer id, Integer userId);
    
    /**
     * 发布动态
     * @param moment 动态信息
     * @return 新增的动态
     */
    Moment publishMoment(Moment moment);
    
    /**
     * 更新动态
     * @param moment 动态信息
     * @return 是否成功
     */
    boolean updateMoment(Moment moment);
    
    /**
     * 删除动态
     * @param id 动态ID
     * @param userId 当前用户ID
     * @return 是否成功
     */
    boolean deleteMoment(Integer id, Integer userId);
    
    /**
     * 点赞动态
     * @param momentId 动态ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean likeMoment(Integer momentId, Integer userId);
    
    /**
     * 取消点赞
     * @param momentId 动态ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean unlikeMoment(Integer momentId, Integer userId);
} 