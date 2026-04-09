package com.howdev.iam.dto;

import lombok.Data;

@Data
public class UserQueryTempAccessKeyReq {
    private String tempAccessKeyId;
    private String sessionToken;
}
