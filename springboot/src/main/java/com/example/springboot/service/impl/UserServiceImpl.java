package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.common.Constants;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.entity.User;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.entity.UserPhoto;
import com.example.springboot.entity.UserTag;
import com.example.springboot.entity.ChatMessage;
import com.example.springboot.entity.UserMatch;
import com.example.springboot.exception.ServiceException;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.mapper.UserProfileMapper;
import com.example.springboot.mapper.UserPhotoMapper;
import com.example.springboot.mapper.UserTagMapper;
import com.example.springboot.mapper.ChatMessageMapper;
import com.example.springboot.mapper.UserMatchMapper;
import com.example.springboot.service.IUserService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.ZoneId;

/**
 * @Auther: 代刘斌
 * @Date: 2023/6/6 - 06 - 06 - 22:33
 * @Description: com.example.springboot.service.imp
 * @version: 1.0
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Log LOG = Log.get();

    @Resource
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserPhotoMapper userPhotoMapper;

    @Autowired
    private UserTagMapper userTagMapper;

    @Autowired
    private UserMatchMapper userMatchMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public UserDTO login(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if (one != null) {
            BeanUtil.copyProperties(one, userDTO, true);
            // 设置token
            String token = TokenUtils.genToken(one.getId().toString(), one.getPassword());
            userDTO.setToken(token);
            
            // 登录成功后，尝试更新用户最后活跃时间
            try {
                 UserProfile profile = userProfileMapper.getById(one.getId());
                 if (profile != null) {
                     userProfileMapper.updateLastActiveTime(one.getId());
                 } else {
                     // 如果 profile 不存在，可能需要创建或记录日志
                     LOG.warn("用户 {} 登录成功，但未找到对应的 UserProfile 记录", one.getId());
                 }
            } catch (Exception e) {
                LOG.error("更新用户 {} 最后活跃时间失败", one.getId(), e);
            }
            
            return userDTO;
        } else {
            throw new ServiceException(Constants.CODE_600, "用户名或密码错误");
        }
    }

    @Override
    public User register(UserDTO userDTO) {
        User one = getUserInfo(userDTO);
        if (one == null) {
            one = new User();
            BeanUtil.copyProperties(userDTO, one, true);
            // 可以在这里设置默认昵称、头像等
            if (one.getNickname() == null) {
                one.setNickname("新用户" + System.currentTimeMillis() % 10000); // 示例默认昵称
            }
            save(one);  // 把 copy完之后的用户对象存储到数据库, ID 会自动生成

            // 同时创建关联的 UserProfile 记录 (基础信息)
             try {
                UserProfile userProfile = new UserProfile();
                userProfile.setId(one.getId()); // 使用 User 表新生成的 ID
                 // 假设 userDTO 包含 openId (需要前端注册时传递)
                 // userProfile.setOpenId(userDTO.getOpenId()); // TODO: 确保 UserDTO 有 openId 字段并从前端获取
                 userProfile.setOpenId("temp_openid_" + one.getId()); // 临时 OpenID，需要后续从小程序获取真实 openid 更新
                 userProfile.setNickname(one.getNickname()); 
                 userProfile.setAvatarUrl(one.getAvatarUrl()); 
                 userProfile.setIsProfileCompleted(false); // 初始状态为未完善资料
                 userProfileMapper.insert(userProfile);
             } catch (Exception e) {
                 // 处理创建 Profile 失败的情况，可能需要回滚 User 创建或记录错误
                 LOG.error("为新用户 {} 创建 UserProfile 失败", one.getId(), e);
                 // 根据业务决定是否抛出异常或返回错误
                 // throw new ServiceException(Constants.CODE_500, "创建用户附加信息失败");
             }

        } else {
            throw new ServiceException(Constants.CODE_600, "用户名已存在");
        }
        return one;
    }

    @Override
    public Page<User> findPage(Page<User> page, String username, String email, String address) {
        return userMapper.findPage(page, username, email, address);
    }

    private User getUserInfo(UserDTO userDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userDTO.getUsername());
        queryWrapper.eq("password", userDTO.getPassword());
        User one;
        try {
            one = getOne(queryWrapper); // 从数据库查询用户信息
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500, "系统错误");
        }
        return one;
    }
    
    /**
     * 实现获取推荐用户列表逻辑
     */
    @Override
    public List<UserProfile> getRecommendations(Integer userId, Double latitude, Double longitude) {
        // 1. 获取当前用户的资料，主要是为了获取性别偏好等（如果需要）
        UserProfile currentUserProfile = userProfileMapper.getById(userId);
        if (currentUserProfile == null) {
            LOG.warn("无法获取当前用户信息，ID: {}", userId);
            return Collections.emptyList();
        }
        
        // 2. 更新当前用户的经纬度信息（如果提供了）
        boolean locationUpdated = false;
        if (latitude != null && longitude != null) {
            // 避免不必要的数据库更新，可以检查经纬度是否有变化
            if (!latitude.equals(currentUserProfile.getLatitude()) || !longitude.equals(currentUserProfile.getLongitude())) {
                 currentUserProfile.setLatitude(latitude);
                 currentUserProfile.setLongitude(longitude);
                 userProfileMapper.update(currentUserProfile); // 假设 update 方法只更新非空字段
                 locationUpdated = true;
            }
        }
        
        // 3. 更新当前用户的最后活跃时间
        // 放在获取推荐之前或之后都可以，这里放在前面
         try {
             userProfileMapper.updateLastActiveTime(userId);
         } catch (Exception e) {
             LOG.error("更新用户 {} 最后活跃时间失败", userId, e);
         }

        // 4. 确定推荐用户的性别
        // TODO: 实现更灵活的性别偏好设置，例如从用户配置读取
        String targetGender = "female"; // 默认推荐异性
        if ("female".equalsIgnoreCase(currentUserProfile.getGender())) {
            targetGender = "male";
        } else if ("male".equalsIgnoreCase(currentUserProfile.getGender())) {
             targetGender = "female";
        } else {
            // 如果当前用户性别未知或为其他，可以推荐所有性别或根据其他规则
            targetGender = null; // 推荐所有性别
        }

        // 5. 调用 Mapper 获取推荐用户 ID 列表或基础信息
        // TODO: 完善推荐算法，加入更多因素：标签匹配度、共同兴趣、活跃度、排除已喜欢/不喜欢/已匹配用户等
        int recommendLimit = 10; // 每次推荐数量
        List<UserProfile> recommendations = userProfileMapper.getRecommendProfiles(
                userId, targetGender, latitude, longitude, recommendLimit);

        // 6. 填充每个推荐用户的详细信息（照片、标签、年龄、距离）
        if (recommendations != null && !recommendations.isEmpty()) {
            for (UserProfile profile : recommendations) {
                // 填充照片 (只取 URL)，并确保第一个是头像
                List<UserPhoto> photos = userPhotoMapper.getByUserId(profile.getId());
                if (photos != null && !photos.isEmpty()) {
                    profile.setPhotos(photos.stream()
                                           .sorted((p1, p2) -> Integer.compare(p1.getOrderNum() != null ? p1.getOrderNum() : 99, 
                                                                               p2.getOrderNum() != null ? p2.getOrderNum() : 99)) // 按 orderNum 排序
                                           .map(UserPhoto::getPhotoUrl)
                                           .collect(Collectors.toList()));
                    // 确保 avatarUrl 是第一张照片
                    profile.setAvatarUrl(profile.getPhotos().get(0));
                } else {
                     profile.setPhotos(Collections.emptyList());
                     // 如果 photos 为空，avatarUrl 可能也需要设为 null 或默认值
                     // profile.setAvatarUrl(null); 
                }

                // 填充标签 (只取名称)
                List<UserTag> tags = userTagMapper.getByUserId(profile.getId());
                if (tags != null) {
                    profile.setTags(tags.stream().map(UserTag::getTagName).collect(Collectors.toList()));
                } else {
                     profile.setTags(Collections.emptyList());
                }

                // 计算年龄
                if (profile.getBirthdate() != null) {
                     try {
                        LocalDate birthDate = profile.getBirthdate().toInstant()
                                                     .atZone(java.time.ZoneId.systemDefault())
                                                     .toLocalDate();
                        profile.setAge(Period.between(birthDate, LocalDate.now()).getYears());
                    } catch (Exception e) {
                         LOG.error("计算用户 {} 年龄失败", profile.getId(), e);
                         profile.setAge(null);
                    }
                } else {
                    profile.setAge(null);
                }

                // 计算距离 (如果提供了经纬度)
                if (latitude != null && longitude != null && profile.getLatitude() != null && profile.getLongitude() != null) {
                     profile.setDistance(calculateDistance(latitude, longitude, profile.getLatitude(), profile.getLongitude()));
                } else {
                    profile.setDistance(null); // 或设为默认值，例如 -1 或 "未知"
                }
            }
        }

        return recommendations == null ? Collections.emptyList() : recommendations;
    }

    /**
     * 计算两个经纬度之间的距离（单位：公里）
     * 使用 Haversine 公式
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 地球半径，单位公里

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 格式化距离，保留一位小数
        return Math.round((R * c) * 10.0) / 10.0;
    }

    /**
     * 实现获取用户匹配列表逻辑
     */
    @Override
    public List<UserMatch> getMatches(Integer userId) {
        // 1. 获取当前用户的所有匹配记录
        List<UserMatch> matches = userMatchMapper.getMatchesByUserId(userId);

        if (matches != null && !matches.isEmpty()) {
            for (UserMatch match : matches) {
                // 2. 确定匹配对方的用户ID
                Integer matchedUserId = match.getUserId().equals(userId) ? match.getMatchedUserId() : match.getUserId();

                // 3. 获取匹配对方的用户 Profile 信息
                UserProfile matchedUserProfile = userProfileMapper.getById(matchedUserId);
                if (matchedUserProfile != null) {
                     // 填充基本信息
                    match.setMatchedUserProfile(matchedUserProfile);
                    // ... (填充照片的注释保持不变)
                } else {
                    LOG.warn("找不到用户 {} 的匹配对象 {} 的 Profile 信息", userId, matchedUserId);
                    // match.setMatchedUserProfile(null); 
                }

                // 4. 获取该匹配的最后一条聊天消息
                ChatMessage lastMessage = chatMessageMapper.getLastMessageByMatchId(match.getId());
                if (lastMessage != null) {
                    // 格式化时间戳
                    lastMessage.setFormattedTime(formatTimestamp(lastMessage.getSendTime()));
                    match.setLastMessage(lastMessage);
                } else {
                    match.setLastMessage(null);
                }

                // 5. 获取当前用户在该匹配中的未读消息数
                int unreadCount = chatMessageMapper.getUnreadCount(match.getId(), userId);
                match.setUnreadCount(unreadCount);
            }
        }

        return matches == null ? Collections.emptyList() : matches;
    }
    
    /**
     * 格式化时间戳用于聊天列表显示
     * @param timestamp 时间
     * @return 格式化后的字符串，例如 "15:30", "昨天 10:20", "04-20 09:00"
     */
    private String formatTimestamp(Date timestamp) {
        if (timestamp == null) {
            return "";
        }

        long nowMillis = System.currentTimeMillis();
        long msgMillis = timestamp.getTime();
        LocalDate nowDate = LocalDate.now();
        LocalDate msgDate = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        long oneDayMillis = 24 * 60 * 60 * 1000;
        long yesterdayStartMillis = nowDate.minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long todayStartMillis = nowDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat yesterdayFormat = new SimpleDateFormat("昨天 HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm"); // 或 "yyyy-MM-dd HH:mm"

        if (msgMillis >= todayStartMillis) {
            // 今天
            return timeFormat.format(timestamp);
        } else if (msgMillis >= yesterdayStartMillis) {
            // 昨天
            return yesterdayFormat.format(timestamp);
        } else {
            // 更早 (同一年可以省略年份)
            // 如果需要显示年份，可以使用 "yyyy-MM-dd HH:mm"
             if (nowDate.getYear() == msgDate.getYear()) {
                 return dateFormat.format(timestamp);
             } else {
                 return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
             }
        }
    }

    /**
     * 获取用户详细资料，包括照片和标签
     */
    @Override
    public UserProfile getUserProfileDetail(Integer userId) {
        UserProfile userProfile = userProfileMapper.getById(userId);
        if (userProfile == null) {
            return null; // 或者抛出异常，取决于业务需求
        }
        
        // 获取用户照片列表
        List<UserPhoto> photos = userPhotoMapper.getByUserId(userId);
        if (photos != null && !photos.isEmpty()) {
            userProfile.setPhotos(photos.stream()
                                      .sorted((p1, p2) -> Integer.compare(p1.getOrderNum() != null ? p1.getOrderNum() : 99, 
                                                                          p2.getOrderNum() != null ? p2.getOrderNum() : 99))
                                      .map(UserPhoto::getPhotoUrl)
                                      .collect(Collectors.toList()));
            // 确保 avatarUrl 不为空
            if (userProfile.getAvatarUrl() == null && !userProfile.getPhotos().isEmpty()) {
                userProfile.setAvatarUrl(userProfile.getPhotos().get(0));
            }
        } else {
            userProfile.setPhotos(Collections.emptyList());
        }
        
        // 获取用户标签列表
        List<UserTag> tags = userTagMapper.getByUserId(userId);
        if (tags != null && !tags.isEmpty()) {
            userProfile.setTags(tags.stream()
                                    .map(UserTag::getTagName)
                                    .collect(Collectors.toList()));
        } else {
            userProfile.setTags(Collections.emptyList());
        }
        
        // 计算年龄（如果有生日信息）
        if (userProfile.getBirthdate() != null) {
            try {
                LocalDate birthDate = userProfile.getBirthdate().toInstant()
                                               .atZone(ZoneId.systemDefault())
                                               .toLocalDate();
                userProfile.setAge(Period.between(birthDate, LocalDate.now()).getYears());
            } catch (Exception e) {
                LOG.error("计算用户 {} 年龄时出错", userId, e);
                userProfile.setAge(null);
            }
        }
        
        return userProfile;
    }

    /**
     * 更新用户资料
     */
    @Override
    public boolean updateUserProfile(UserProfile userProfile) {
        if (userProfile == null || userProfile.getId() == null) {
            LOG.error("传入的用户资料为空或缺少ID");
            return false;
        }
        
        // 防止更新敏感字段
        userProfile.setOpenId(null); // 防止修改openId
        userProfile.setRegisterTime(null); // 防止修改注册时间
        
        // 1. 更新基本资料
        int result = userProfileMapper.update(userProfile);
        if (result <= 0) {
            LOG.error("更新用户 {} 基本资料失败", userProfile.getId());
            return false;
        }
        
        // 2. 更新标签（如果提供了）
        if (userProfile.getTags() != null && !userProfile.getTags().isEmpty()) {
            try {
                // 删除旧标签
                userTagMapper.deleteByUserId(userProfile.getId());
                
                // 插入新标签
                List<UserTag> tags = userProfile.getTags().stream()
                        .map(tagName -> {
                            UserTag tag = new UserTag();
                            tag.setUserId(userProfile.getId());
                            tag.setTagName(tagName);
                            return tag;
                        })
                        .collect(Collectors.toList());
                
                if (!tags.isEmpty()) {
                    userTagMapper.batchInsert(tags);
                }
            } catch (Exception e) {
                LOG.error("更新用户 {} 标签失败", userProfile.getId(), e);
                // 这里可以选择继续执行或者返回失败，取决于业务需求
                // return false;
            }
        }
        
        // 3. 更新照片（照片通常通过单独的文件上传接口处理，这里不实现）
        // 如果需要处理照片顺序或删除照片的逻辑，可以在这里添加
        
        // 4. 如果是首次完善资料，将isProfileCompleted更新为true
        if (Boolean.TRUE.equals(userProfile.getIsProfileCompleted())) {
            // 确认是否首次完善资料
            UserProfile existingProfile = userProfileMapper.getById(userProfile.getId());
            if (existingProfile != null && !Boolean.TRUE.equals(existingProfile.getIsProfileCompleted())) {
                // 更新完成状态
                UserProfile updateStatus = new UserProfile();
                updateStatus.setId(userProfile.getId());
                updateStatus.setIsProfileCompleted(true);
                userProfileMapper.update(updateStatus);
                
                // 这里还可以添加首次完善资料的奖励逻辑等
            }
        }
        
        return true;
    }
}
