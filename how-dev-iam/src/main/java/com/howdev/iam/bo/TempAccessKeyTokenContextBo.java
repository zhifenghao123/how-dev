package com.howdev.iam.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TempAccessKeyTokenContextBo {
    private String userId;
    private String userType;
    private List<PermissionBo> permissions;
    private long expireTime;
}
