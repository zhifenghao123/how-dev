package com.howdev.jwtauth.dto;

import com.howdev.jwtauth.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class ListUsersResp {
    private List<User> users;
    private long total;
}
