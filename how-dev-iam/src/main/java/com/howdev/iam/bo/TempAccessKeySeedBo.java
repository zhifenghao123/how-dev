package com.howdev.iam.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TempAccessKeySeedBo {
    private String accessKeyId;
    private String secretAccessKey;
    private String sessionToken;
    private long expireTime;
}
