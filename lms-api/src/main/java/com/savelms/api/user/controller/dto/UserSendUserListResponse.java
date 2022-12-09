package com.savelms.api.user.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
public class UserSendUserListResponse {

    @Schema(description = "요소 개수" , example = "3")
    @NotNull
    public Integer count;

    @Schema(description = "요소 리스트" , example = "[{}, {}, {}]")
    public List<UserResponseDto> users;
}
