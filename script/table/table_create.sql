/* CREATE DATABASE  */
-- CREATE DATABASE IF NOT EXISTS certapp DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
-- USE `certapp`;

CREATE TABLE IF NOT EXISTS `tb_user`(
   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
   `name` VARCHAR(32) NOT NULL COMMENT '姓名',
   `pwd` varchar(250) NOT NULL COMMENT '登录密码',
   PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='账号表';


CREATE TABLE IF NOT EXISTS `tb_cert_config`(
   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
   `cert_name` VARCHAR(32) DEFAULT '' COMMENT '证书名称',
   `base_map` LONGTEXT NOT NULL COMMENT '底图（base64）',
   `filed_font` VARCHAR(100) DEFAULT '' COMMENT '字段字体',
   `qr_code_url` VARCHAR(256) DEFAULT '' COMMENT '二维码根URL',
   `qr_code_local` VARCHAR(100) DEFAULT '' COMMENT '二维码位置',
   `number_local` VARCHAR(100) DEFAULT '' COMMENT '编号位置',
   `num_font` VARCHAR(64) DEFAULT '' COMMENT '编号字体',
   `pic_preview` LONGTEXT NOT NULL COMMENT '预览图（base64）',
   `cert_desc` varchar(256) COMMENT '证书描述',
   `cert_real` int(1) DEFAULT 0 COMMENT '1为固定模板，不能删除',
   `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
   PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='证书配置信息表';


CREATE TABLE IF NOT EXISTS `tb_filed_info`(
   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
   `cert_config_id` int(11) NOT NULL COMMENT '证书配置id',
   `filed_serial` VARCHAR(32) DEFAULT '' COMMENT '字段序号',
   `filed_name` VARCHAR(32) DEFAULT '' COMMENT '字段名称',
   `filed_name_zh` VARCHAR(32) DEFAULT '' COMMENT '字段中文名称',
   `filed_local` VARCHAR(100) DEFAULT '' COMMENT '字段位置',
   `show_enable` int(1) DEFAULT 1 COMMENT '是否显示',
   `filed_demo` VARCHAR(32) DEFAULT '' COMMENT '字段样例值',
   PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='证书填充字段配置信息表';


CREATE TABLE IF NOT EXISTS `tb_cert_info`(
   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
   `cert_config_id` int(11) NOT NULL COMMENT '证书配置id',
   `cert_id` VARCHAR(64) DEFAULT '' COMMENT '证书编号（MD5值）',
   `filed_info` VARCHAR(1024) DEFAULT '' COMMENT '填充字段信息(String列表转json)',
   `cert_pic` LONGTEXT DEFAULT NULL COMMENT '证书图片（base64）',
   `cert_hash` VARCHAR(100) DEFAULT '' COMMENT '证书哈希',
   `tx_hash` VARCHAR(100) DEFAULT '' COMMENT '交易哈希',
   `cert_url` varchar(1024) COMMENT '证书url',
   `cert_time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '证书上链时间',
   `cert_state` int(1) DEFAULT 0 COMMENT '证书上链状态（1-已上链；0-未上链）；2-已发布',
   `publish_state` int(1) DEFAULT 0 COMMENT '发布状态',
   `search_filed` VARCHAR(64) DEFAULT '' COMMENT '搜索字段',
   PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='证书详情信息表';


CREATE TABLE IF NOT EXISTS `tb_query_page`(
   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
   `cert_config_id` int(11) NOT NULL COMMENT '证书配置id',
   `banner` LONGTEXT NOT NULL COMMENT '横幅图片（base64）',
   `cert_org` VARCHAR(64) DEFAULT '' COMMENT '发证机构',
   `search_hint` VARCHAR(64) DEFAULT '' COMMENT '搜索提示语',
   `bottom_log` LONGTEXT NOT NULL COMMENT '底部log（base64）',
   `explain_doc` VARCHAR(64) DEFAULT '' COMMENT '技术支持说明文案',
   `filed_serial` VARCHAR(32) DEFAULT '' COMMENT '字段序号',
   PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='查询页表';


CREATE TABLE IF NOT EXISTS `tb_query_result_page`(
   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
   `cert_config_id` int(11) NOT NULL COMMENT '证书配置id',
   `title` VARCHAR(64) DEFAULT '' COMMENT '标题',
   `enable_fields` VARCHAR(5000) DEFAULT '' COMMENT '显示字段',
   `bottom_log` LONGTEXT NOT NULL COMMENT '底部log（base64）',
   `explain_doc` VARCHAR(1024) DEFAULT '' COMMENT '技术支持说明文案',
   PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='查询结果页表';


CREATE TABLE IF NOT EXISTS `tb_code`(
   `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '编号',
   `code` VARCHAR(32) NOT NULL COMMENT '验证码',
   PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='验证码表';
