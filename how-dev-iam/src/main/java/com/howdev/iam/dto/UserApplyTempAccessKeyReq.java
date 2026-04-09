package com.howdev.iam.dto;

import lombok.Data;

@Data
public class UserApplyTempAccessKeyReq {
    private String userId;
    private long durationSeconds;
}
