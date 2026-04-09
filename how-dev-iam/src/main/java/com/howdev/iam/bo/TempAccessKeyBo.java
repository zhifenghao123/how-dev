package com.howdev.iam.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TempAccessKeyBo {
    private String accessKeyId;
    private String secretAccessKey;
    private TempAccessKeyTokenContextBo tokenContext;
    private long expireTime;
}
