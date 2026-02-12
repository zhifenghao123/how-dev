package com.howdev.jwtauth.constant;

public class RegexPatternConst {

    // 名称校验规则： MinLength-MaxLength个字符之间，只能包括英文字母、数字和+=.@_-
    public static final String NAME_PATTERN = "^[a-zA-Z0-9+=.@_-]{4,64}$";

}
