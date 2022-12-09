package com.savelms.core.vacation.domain.repository;

import com.savelms.core.vacation.domain.entity.Vacation;

import java.util.Optional;

public interface VacationQueryRepository {

    Optional<Vacation> findFirstByUserApiId(String userApiId);
}
