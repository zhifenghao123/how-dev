package com.howdev.iam.dto;

import lombok.Data;

@Data
public class TempAccessKeyResp {
    private String accessKeyId;
    private String secretAccessKey;
    private String sessionToken;
    private long expireTime;
}
