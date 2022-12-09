package com.savelms.core.user.domain.repository.dto;

import com.savelms.core.SortTypeEnum;
import com.savelms.core.exception.QueryStringFormatException;
import com.savelms.core.user.UserSortFieldEnum;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSortRuleDto {

    private static final Set<String> fields = Set.of(UserSortFieldEnum.NICKNAME.getValue(), UserSortFieldEnum.CREATEDDATE.getValue());
    private static final Set<String> sortTypes = Set.of(SortTypeEnum.ASC.getValue(), SortTypeEnum.DESC.getValue());

    private String fieldName;
    private String sortType;

}
