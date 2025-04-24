-- 创建用户喜欢关系表
CREATE TABLE IF NOT EXISTS `like` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `liked_user_id` int(11) NOT NULL COMMENT '被喜欢用户ID',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '类型：1-喜欢，0-不喜欢',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_liked` (`user_id`,`liked_user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_liked_user_id` (`liked_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户喜欢关系表';

-- 添加注释
ALTER TABLE `like` 
COMMENT = '用户喜欢关系表'; 