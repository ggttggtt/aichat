package com.example.springboot.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.springboot.entity.Moment;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.mapper.MomentLikeMapper;
import com.example.springboot.mapper.MomentMapper;
import com.example.springboot.mapper.UserProfileMapper;
import com.example.springboot.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户动态服务实现类
 */
@Service
public class MomentServiceImpl implements MomentService {

    @Autowired
    private MomentMapper momentMapper;
    
    @Autowired
    private UserProfileMapper userProfileMapper;
    
    @Autowired(required = false)
    private MomentLikeMapper momentLikeMapper;

    @Override
    public List<Moment> listMoments(Integer userId, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        List<Moment> moments = momentMapper.listMoments(userId, offset, size);
        return processMoments(moments, userId);
    }

    @Override
    public List<Moment> listFriendMoments(Integer userId, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        List<Moment> moments = momentMapper.listFriendMoments(userId, offset, size);
        return processMoments(moments, userId);
    }

    @Override
    public List<Moment> listUserMoments(Integer userId, Integer visitorId, Integer page, Integer size) {
        Integer offset = (page - 1) * size;
        List<Moment> moments = momentMapper.listUserMoments(userId, visitorId, offset, size);
        return processMoments(moments, visitorId);
    }

    @Override
    public Moment getMomentById(Integer id, Integer userId) {
        Moment moment = momentMapper.getMomentById(id);
        if (moment != null) {
            processMoment(moment, userId);
        }
        return moment;
    }

    @Override
    @Transactional
    public Moment publishMoment(Moment moment) {
        // 初始化点赞数和评论数
        moment.setLikeCount(0);
        moment.setCommentCount(0);
        
        // 插入数据
        momentMapper.insert(moment);
        
        // 返回完整的动态信息
        return getMomentById(moment.getId(), moment.getUserId());
    }

    @Override
    public boolean updateMoment(Moment moment) {
        // 检查权限（确保只能更新自己的动态）
        Moment existingMoment = momentMapper.getMomentById(moment.getId());
        if (existingMoment == null || !existingMoment.getUserId().equals(moment.getUserId())) {
            return false;
        }
        
        return momentMapper.update(moment) > 0;
    }

    @Override
    @Transactional
    public boolean deleteMoment(Integer id, Integer userId) {
        // 检查权限（确保只能删除自己的动态）
        Moment moment = momentMapper.getMomentById(id);
        if (moment == null || !moment.getUserId().equals(userId)) {
            return false;
        }
        
        // 删除动态
        return momentMapper.delete(id) > 0;
    }

    @Override
    @Transactional
    public boolean likeMoment(Integer momentId, Integer userId) {
        // 检查动态是否存在
        Moment moment = momentMapper.getMomentById(momentId);
        if (moment == null) {
            return false;
        }
        
        // 检查是否已经点赞
        if (momentLikeMapper != null && momentLikeMapper.checkLike(momentId, userId) != null) {
            return false;
        }
        
        // 增加点赞数
        momentMapper.increaseLikeCount(momentId);
        
        // 记录点赞关系
        if (momentLikeMapper != null) {
            momentLikeMapper.insert(momentId, userId);
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean unlikeMoment(Integer momentId, Integer userId) {
        // 检查动态是否存在
        Moment moment = momentMapper.getMomentById(momentId);
        if (moment == null) {
            return false;
        }
        
        // 检查是否已经点赞
        if (momentLikeMapper != null && momentLikeMapper.checkLike(momentId, userId) == null) {
            return false;
        }
        
        // 减少点赞数
        momentMapper.decreaseLikeCount(momentId);
        
        // 删除点赞关系
        if (momentLikeMapper != null) {
            momentLikeMapper.delete(momentId, userId);
        }
        
        return true;
    }
    
    /**
     * 处理动态列表，加载用户信息和图片列表
     */
    private List<Moment> processMoments(List<Moment> moments, Integer currentUserId) {
        if (moments == null || moments.isEmpty()) {
            return new ArrayList<>();
        }
        
        for (Moment moment : moments) {
            processMoment(moment, currentUserId);
        }
        
        return moments;
    }
    
    /**
     * 处理单个动态，加载用户信息和图片列表
     */
    private void processMoment(Moment moment, Integer currentUserId) {
        // 加载用户信息
        UserProfile userProfile = userProfileMapper.getById(moment.getUserId());
        moment.setUserProfile(userProfile);
        
        // 转换图片URL列表
        if (moment.getImageUrls() != null && !moment.getImageUrls().isEmpty()) {
            try {
                List<String> images = JSON.parseArray(moment.getImageUrls(), String.class);
                moment.setImages(images);
            } catch (Exception e) {
                moment.setImages(new ArrayList<>());
            }
        } else {
            moment.setImages(new ArrayList<>());
        }
        
        // 检查当前用户是否点赞
        if (currentUserId != null && momentLikeMapper != null) {
            boolean isLiked = momentLikeMapper.checkLike(moment.getId(), currentUserId) != null;
            moment.setIsLiked(isLiked);
        } else {
            moment.setIsLiked(false);
        }
    }
} 