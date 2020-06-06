/*
 Navicat Premium Data Transfer

 Source Server         : localhost_mysql8
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost:3306
 Source Schema         : dbxc

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 06/06/2020 09:33:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
CREATE DATABASE `dbxc` CHARACTER SET 'utf8';
-- ----------------------------
-- Table structure for base_user
-- ----------------------------
use dbxc;
CREATE TABLE dbxc.`base_user`  (
  `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `attrs` json NULL,
  `mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `nick_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_o6melywlekduad7dk8mog3jyb`(`mobile`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of base_user
-- ----------------------------
INSERT INTO dbxc.`base_user` VALUES ('3d3a8382-dcfb-4e9f-b2ea-8c7138f469ca', '{\"companyName\": \"无厘头公司\", \"companyType\": \"1\"}', '13800138000', '超级管理员');

SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- Table structure for undo_log 建立seata分布式同步锁所用的回滚日志表
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'increment id',
  `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime(0) NOT NULL COMMENT 'create datetime',
  `log_modified` datetime(0) NOT NULL COMMENT 'modify datetime',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `ux_undo_log`(`xid`, `branch_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'AT transaction mode undo table' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

