-- ----------------------------
-- Table structure for scheduled_task
-- ----------------------------
DROP TABLE IF EXISTS `scheduled_task`;
CREATE TABLE `scheduled_task` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `app_name` varchar(50) DEFAULT '' COMMENT '应用标识',
  `job_class` varchar(80) COMMENT '任务接口类名',
  `job_method` varchar(80) COMMENT '任务方法名',
  `job_arguments` varchar(2048) COMMENT '任务参数',
  `job_group` varchar(80) COMMENT '任务分组',
  `job_name` varchar(80) COMMENT '任务名称',
  `job_status` tinyint(1) DEFAULT '1' COMMENT '任务状态 1-启用 2-停用',
  `cron_expression` varchar(80) COMMENT '时间表达式',
  `description` varchar(64) DEFAULT '' COMMENT '任务描述',
  `last_run_timestamp` bigint(20) DEFAULT '-1' COMMENT '最后执行时间戳',
  `create_by` varchar(50) DEFAULT '' COMMENT '创建人ID',
  `create_name` varchar(50) DEFAULT '' COMMENT '创建人名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT '' COMMENT '最后修改人ID',
  `update_name` varchar(50) DEFAULT '' COMMENT '最后修改人名称',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '删除标记 0正常 1-删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_project` (`app_name`) USING BTREE,
  KEY `idx_job_class_method` (`job_class`,`job_method`) USING BTREE,
  KEY `idx_job_name` (`job_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='任务表';