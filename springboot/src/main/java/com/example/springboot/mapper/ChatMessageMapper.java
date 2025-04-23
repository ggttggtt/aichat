package com.example.springboot.mapper;

import com.example.springboot.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    
    ChatMessage getById(Integer id);
    
    List<ChatMessage> getMessagesByMatchId(Integer matchId);
    
    ChatMessage getLastMessageByMatchId(Integer matchId);
    
    int getUnreadCount(@Param("matchId") Integer matchId, @Param("receiverId") Integer receiverId);
    
    int insert(ChatMessage chatMessage);
    
    int markAsRead(@Param("matchId") Integer matchId, @Param("receiverId") Integer receiverId);
} 