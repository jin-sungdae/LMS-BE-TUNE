package com.savelms.api.user.userteam.service;


import com.savelms.core.team.TeamEnum;
import com.savelms.core.team.domain.entity.UserTeam;
import com.savelms.core.team.domain.repository.UserTeamRepository;
import com.savelms.core.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserTeamService {

    private final UserTeamRepository userTeamRepository;

    public Map<Long, TeamEnum> findAllUserTeamByDateAndAttendStatus(LocalDate date) {
        Map<Long, TeamEnum> userTeams = new HashMap<>();
        Map<User, List<UserTeam>> userAndUserTeams
                = userTeamRepository.findAllByDateAndAttendStatus(LocalDateTime.of(date, LocalTime.MAX))
                    .stream().collect(Collectors.groupingBy(UserTeam::getUser));

        for (User user : userAndUserTeams.keySet()) {
            UserTeam userTeam = userAndUserTeams.get(user).stream().max(Comparator.comparing(UserTeam::getCreatedAt)).get();
            userTeams.put(user.getId(), userTeam.getTeam().getValue());
        }
        return userTeams;
    }
}
