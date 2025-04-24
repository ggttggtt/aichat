package com.example.springboot.mapper;

import com.example.springboot.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface ChatMessageMapper {
    
    /**
     * 获取匹配关系的最后一条消息
     * @param matchId 匹配关系ID
     * @return 最后一条消息
     */
    ChatMessage getLastMessageByMatchId(@Param("matchId") Integer matchId);
    
    /**
     * 获取匹配关系的未读消息数
     * @param matchId 匹配关系ID
     * @param userId 当前用户ID
     * @return 未读消息数
     */
    int getUnreadCount(@Param("matchId") Integer matchId, @Param("userId") Integer userId);
    
    /**
     * 根据匹配关系ID获取聊天记录
     * @param matchId 匹配关系ID
     * @return 聊天记录列表
     */
    List<ChatMessage> getMessagesByMatchId(@Param("matchId") Integer matchId);
    
    /**
     * 添加消息
     * @param message 消息信息
     * @return 影响的行数
     */
    int insert(ChatMessage message);
    
    /**
     * 更新消息已读状态
     * @param matchId 匹配关系ID
     * @param userId 当前用户ID
     * @return 影响的行数
     */
    int updateReadStatus(@Param("matchId") Integer matchId, @Param("userId") Integer userId);
} 