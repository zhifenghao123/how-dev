package com.howdev.iam.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserBo {
    private Long id;

    private String userId;

    private String username;

    private String password;

    private String name;

    private String status;

    private String createTime;

    private String updateTime;
}
