package com.savelms.core.team.domain.repository;

import com.savelms.core.team.TeamEnum;
import com.savelms.core.team.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface
TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByValue(TeamEnum teamEnum);
}
