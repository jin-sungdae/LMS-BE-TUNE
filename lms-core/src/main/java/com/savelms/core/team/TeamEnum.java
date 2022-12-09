package com.savelms.core.team;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.savelms.core.user.role.RoleEnum;
import java.util.HashMap;
import java.util.Map;
import jdk.jshell.Snippet.Status;

public enum TeamEnum {
    NONE, RED, BLUE;

    @JsonCreator
    public static TeamEnum from(String s) {
        return TeamEnum.valueOf(s.toUpperCase());
    }

}
