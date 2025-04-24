-- 创建动态表
CREATE TABLE IF NOT EXISTS `moment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `content` text COMMENT '动态内容',
  `image_urls` text COMMENT '图片URL列表，JSON格式',
  `location` varchar(255) DEFAULT NULL COMMENT '位置信息',
  `visibility` tinyint(1) NOT NULL DEFAULT '0' COMMENT '可见性：0-所有人可见，1-仅自己可见，2-部分可见',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户动态表';

-- 创建动态点赞关系表
CREATE TABLE IF NOT EXISTS `moment_like` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `moment_id` int(11) NOT NULL COMMENT '动态ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_moment_user` (`moment_id`,`user_id`),
  KEY `idx_moment_id` (`moment_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态点赞关系表';

-- 创建动态评论表
CREATE TABLE IF NOT EXISTS `moment_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `moment_id` int(11) NOT NULL COMMENT '动态ID',
  `user_id` int(11) NOT NULL COMMENT '评论者ID',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `parent_id` int(11) DEFAULT NULL COMMENT '父评论ID，回复评论时使用',
  `reply_user_id` int(11) DEFAULT NULL COMMENT '被回复的用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_moment_id` (`moment_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态评论表';

-- 插入示例数据：动态
INSERT INTO `moment` (user_id, content, image_urls, location, visibility, like_count, comment_count, create_time, update_time)
VALUES 
(1, '今天天气真好，出去走走', '[\"https://yanxuesi.oss-cn-chengdu.aliyuncs.com/aichat/moments/image1.jpg\"]', '成都市锦江区', 0, 2, 1, NOW(), NOW()),
(2, '分享一本好书《活着》', '[\"https://yanxuesi.oss-cn-chengdu.aliyuncs.com/aichat/moments/image2.jpg\"]', '北京市海淀区', 0, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, '健身打卡第30天', '[\"https://yanxuesi.oss-cn-chengdu.aliyuncs.com/aichat/moments/image3.jpg\", \"https://yanxuesi.oss-cn-chengdu.aliyuncs.com/aichat/moments/image4.jpg\"]', '上海市静安区', 0, 3, 2, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, '私密的一条动态', NULL, NULL, 1, 0, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, '尝试了一家新餐厅，味道不错', '[\"https://yanxuesi.oss-cn-chengdu.aliyuncs.com/aichat/moments/image5.jpg\"]', '重庆市解放碑', 0, 1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY));

-- 插入示例数据：点赞关系
INSERT INTO `moment_like` (moment_id, user_id, create_time)
VALUES 
(1, 2, NOW()),
(1, 3, NOW()),
(2, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 4, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 1, DATE_SUB(NOW(), INTERVAL 4 DAY));

-- 插入示例数据：评论
INSERT INTO `moment_comment` (moment_id, user_id, content, parent_id, reply_user_id, create_time, update_time)
VALUES 
(1, 3, '真好看，去哪里拍的？', NULL, NULL, NOW(), NOW()),
(3, 1, '坚持就是胜利！', NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 2, '加油！', NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 2, '下次一起去！', NULL, NULL, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)); 