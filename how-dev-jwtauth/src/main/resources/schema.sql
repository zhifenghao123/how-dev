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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE `t_role` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                          `name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '角色名称，唯一，且不可修改',
                          `description` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '角色描述',
                          `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态：ENABLE-启用，DISABLE-禁用',
                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_name` (`name`),
                          KEY `idx_status` (`status`),
                          KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE `t_permission` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '权限名称，唯一，且不可修改',
                                `url` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '权限URL',
                                `description` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '权限描述',
                                `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态：ENABLE-启用，DISABLE-禁用',
                                `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_name` (`name`),
                                KEY `idx_status` (`status`),
                                KEY `idx_url` (`url`),
                                KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE `t_role_permission` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `role_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '角色名称',
                                     `permission_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '权限名称',
                                     `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态：ENABLE-启用，DISABLE-禁用',
                                     `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_role_permission` (`role_name`, `permission_name`),
                                     KEY `idx_role_name` (`role_name`),
                                     KEY `idx_permission_name` (`permission_name`),
                                     KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 用户角色关联表
CREATE TABLE `t_user_role` (
                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `user_id` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户ID',
                               `role_name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '角色名称',
                               `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态：ENABLE-启用，DISABLE-禁用',
                               `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_user_role` (`user_id`, `role_name`),
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_role_name` (`role_name`),
                               KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';