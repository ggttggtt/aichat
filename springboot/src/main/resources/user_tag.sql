-- 创建用户标签表
CREATE TABLE IF NOT EXISTS `user_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签表';

-- 插入示例数据
INSERT INTO `user_tag` (user_id, tag_name, create_time, update_time)
VALUES 
-- 用户1的标签
(1, '旅行', NOW(), NOW()),
(1, '摄影', NOW(), NOW()),
(1, '美食', NOW(), NOW()),
(1, '电影', NOW(), NOW()),

-- 用户2的标签
(2, '阅读', NOW(), NOW()),
(2, '音乐', NOW(), NOW()),
(2, '咖啡', NOW(), NOW()),

-- 用户3的标签
(3, '健身', NOW(), NOW()),
(3, '户外', NOW(), NOW()),
(3, '运动', NOW(), NOW()),
(3, '冒险', NOW(), NOW()),

-- 用户4的标签
(4, '音乐', NOW(), NOW()),
(4, '钢琴', NOW(), NOW()),
(4, '古典乐', NOW(), NOW()),

-- 用户5的标签
(5, '美食', NOW(), NOW()),
(5, '烹饪', NOW(), NOW()),
(5, '旅行', NOW(), NOW()); 