/*
 Navicat MySQL Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 17/07/2021 12:59:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for database_lock
-- ----------------------------
DROP TABLE IF EXISTS `database_lock`;
CREATE TABLE `database_lock` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resource` varchar(256) NOT NULL COMMENT '锁定的资源',
  `description` varchar(1024) NOT NULL DEFAULT '' COMMENT '描述',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `updae_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uiq_idx_resource` (`resource`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据库分布式锁表';

SET FOREIGN_KEY_CHECKS = 1;
