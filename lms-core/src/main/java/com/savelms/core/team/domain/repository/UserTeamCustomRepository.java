package com.savelms.core.team.domain.repository;

import com.savelms.core.team.domain.entity.UserTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserTeamCustomRepository {

    private final EntityManager em;

}
