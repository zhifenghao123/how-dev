package com.howdev.iam.dto;

import com.howdev.iam.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class ListUsersResp {
    private List<User> users;
    private long total;
}
