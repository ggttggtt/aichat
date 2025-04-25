package com.example.springboot.mapper;

import com.example.springboot.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 用户资料表 Mapper 接口
 * </p>
 *
 * @author ZXL
 * @since 2025-04-25
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
    
    UserProfile getById(Integer id);
    
    /**
     * 通过openId获取用户资料
     * @param openId 微信openId
     * @return 用户资料
     */
    UserProfile getByOpenId(@Param("openId") String openId);
    
    int insert(UserProfile userProfile);
    
    /**
     * 更新用户资料
     * @param userProfile 用户资料
     * @return 影响的行数
     */
    int update(UserProfile userProfile);
    
    List<UserProfile> getRecommendProfiles(@Param("userId") Integer userId, 
                                         @Param("gender") String gender, 
                                         @Param("latitude") Double latitude, 
                                         @Param("longitude") Double longitude,
                                         @Param("limit") Integer limit);
    
    /**
     * 更新用户最后活跃时间
     * @param userId 用户ID
     * @return 影响的行数
     */
    int updateLastActiveTime(Integer userId);
}
