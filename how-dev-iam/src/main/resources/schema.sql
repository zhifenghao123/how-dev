-- 用户表
CREATE TABLE `t_user` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                          `user_id` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户ID',
                          `username` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户名，唯一，且不可修改',
                          `password` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '密码',
                          `name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '姓名',
                          `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态：ENABLE-启用，DISABLE-禁用',
                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_user_id` (`user_id`),
                          UNIQUE KEY `uk_username` (`username`),
                          KEY `idx_status` (`status`),
                          KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

-- 访问密钥表
CREATE TABLE `t_user_access_key` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `user_id` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户ID',
                                   `access_key_id` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '访问密钥ID',
                                   `secret_access_key` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '加密后的访问密钥',
                                   `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态：ENABLE-启用，DISABLE-禁用',
                                   `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_access_key_id` (`access_key_id`),
                                   KEY `idx_user_id` (`user_id`),
                                   KEY `idx_status` (`status`),
                                   KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户访问密钥表';

-- 访问密钥操作流水表
CREATE TABLE `t_user_access_key_operation` (
                                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                             `user_id` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户ID',
                                             `access_key_id` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '访问密钥ID',
                                             `operation` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '操作类型：CREATE-创建，ENABLE-启用，DISABLE-禁用，DELETE-删除',
                                             `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                             PRIMARY KEY (`id`),
                                             KEY `idx_user_id` (`user_id`),
                                             KEY `idx_access_key_id` (`access_key_id`),
                                             KEY `idx_operation` (`operation`),
                                             KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '访问密钥操作流水表';



-- 临时访问密钥表
CREATE TABLE `t_temp_access_key` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `temp_access_key_id` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '临时访问密钥ID，明文存储',
                                     `temp_access_secret_key` TEXT NOT NULL COMMENT '临时访问密钥Key，加密后存储',
                                     `temp_access_token` TEXT NOT NULL COMMENT '临时访问Token，加密后存储',
                                     `key_create_time` BIGINT NOT NULL DEFAULT 0 COMMENT '临时访问密钥创建时间戳',
                                     `key_expire_time` BIGINT NOT NULL DEFAULT 0 COMMENT '临时访问密钥过期时间戳',
                                     `distribute_base` INT NOT NULL DEFAULT 0 COMMENT '分发基数',
                                     `seed_key_id` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '关联的临时访问密钥种子ID',
                                     `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_temp_access_key_id` (`temp_access_key_id`),
                                     KEY `idx_seed_key_id` (`seed_key_id`),
                                     KEY `idx_key_expire_time` (`key_expire_time`),
                                     KEY `idx_distribute_base` (`distribute_base`),
                                     KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '临时访问密钥表';

-- 临时访问密钥种子表
CREATE TABLE `t_temp_access_key_seed` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `seed_key_id` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '加密密钥ID',
                                          `seed_key` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '加密密钥，16字节可打印字符',
                                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `uk_seed_key_id` (`seed_key_id`),
                                          KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '临时访问密钥种子表';
