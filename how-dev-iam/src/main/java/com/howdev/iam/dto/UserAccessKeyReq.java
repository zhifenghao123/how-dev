package com.howdev.iam.dto;

import lombok.Data;

@Data
public class UserAccessKeyReq {
    private String userId;
    private String accessKeyId;

}
