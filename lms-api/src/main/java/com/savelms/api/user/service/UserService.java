package com.savelms.api.user.service;


import com.savelms.api.auth.config.authorityupdater.AuthoritiesUpdater;
import com.savelms.api.auth.controller.dto.EmailAuthRequestDto;
import com.savelms.api.auth.service.EmailService;
import com.savelms.api.team.service.TeamService;
import com.savelms.api.todo.controller.dto.ListResponse;
import com.savelms.api.user.controller.dto.*;
import com.savelms.api.user.role.service.RoleService;
import com.savelms.core.attendance.domain.entity.Attendance;
import com.savelms.core.attendance.domain.repository.AttendanceRepository;
import com.savelms.core.calendar.domain.entity.Calendar;
import com.savelms.core.calendar.domain.repository.CalendarRepository;
import com.savelms.core.exception.PasswordNotMatchException;
import com.savelms.core.statistical.DayStatisticalData;
import com.savelms.core.statistical.DayStatisticalDataRepository;
import com.savelms.core.team.TeamEnum;
import com.savelms.core.team.domain.entity.Team;
import com.savelms.core.team.domain.entity.UserTeam;
import com.savelms.core.team.domain.repository.TeamRepository;
import com.savelms.core.team.domain.repository.UserTeamRepository;
import com.savelms.core.user.AttendStatus;
import com.savelms.core.user.domain.DuplicateUsernameException;
import com.savelms.core.user.domain.entity.User;
import com.savelms.core.user.domain.repository.UserCustomRepository;
import com.savelms.core.user.domain.repository.UserRepository;
import com.savelms.core.user.emailauth.domain.entity.EmailAuth;
import com.savelms.core.user.emailauth.domain.repository.EmailAuthCustomRepository;
import com.savelms.core.user.emailauth.domain.repository.EmailAuthRepository;
import com.savelms.core.user.role.RoleEnum;
import com.savelms.core.user.role.domain.entity.Role;
import com.savelms.core.user.role.domain.entity.UserRole;
import com.savelms.core.user.role.domain.repository.RoleRepository;
import com.savelms.core.user.role.domain.repository.UserRoleRepository;
import com.savelms.core.vacation.domain.entity.Vacation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparingLong;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final UserCustomRepository userCustomRepository;
    private final RoleService roleService;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final TeamRepository teamRepository;

    private final TeamService teamService;
    private final UserTeamRepository userTeamRepository;

    private final CalendarRepository calendarRepository;

    private final EmailAuthRepository emailAuthRepository;

    private final EmailAuthCustomRepository emailAuthCustomRepository;

    private final EmailService emailService;

    private final AuthoritiesUpdater authoritiesUpdater;

    private final DayStatisticalDataRepository dayStatisticalDataRepository;

    private final AttendanceRepository attendanceRepository;

    /**
     * ????????????
     *
     * @param userSignUpRequest
     * @return
     */
    @Transactional
    public String validateUserNameAndSignUp(UserSignUpRequest userSignUpRequest) {
        validateUsernameDuplicate(userSignUpRequest.getUsername());

        Role defaultRole = roleService.findByValue(RoleEnum.ROLE_UNAUTHORIZED);
        Team defaultTeam = teamService.findByValue(TeamEnum.NONE);
        Calendar calendar = calendarRepository.findByDate(LocalDate.now())
            .orElseThrow(() ->
                new EntityNotFoundException("????????? ????????? ????????????."));
        String encodedPassword = bCryptPasswordEncoder.encode(userSignUpRequest.getPassword());
        User defaultUser = User.createDefaultUser(userSignUpRequest.getUsername(), encodedPassword,
            userSignUpRequest.getUsername() + User.EMAILSUFFIX);
        UserRole.createUserRole(defaultUser, defaultRole, "signUpDefault", true);
        UserTeam.createUserTeam(defaultUser, defaultTeam, "signUpDefault", true);


        User savedUser = userRepository.saveAndFlush(defaultUser);
        EmailAuth emailAuth = emailAuthRepository.save(
            EmailAuth.createEmailAuth(userSignUpRequest.getUsername() + User.EMAILSUFFIX,
                UUID.randomUUID().toString()));

        emailService.send(emailAuth.getEmail(),
            "42Seoul ????????? ?????? ???????????? ????????? ???????????????. ??????????????? ???????????? ????????? ???????????????. 2??? ??? ???????????????.",
            EmailAuth.HOST + EmailAuth.EMAILAUTHPATH + "?id=" + savedUser.getApiId()
                + "&email=" + emailAuth.getEmail()
                + "&authToken=" + emailAuth.getAuthToken()
                );

        return savedUser.getApiId();
    }


    /**
     * ?????? ?????????????????? ?????? ??????
     *
     * @param username
     */
    private void validateUsernameDuplicate(String username) {
        userRepository.findByUsername(username)
            .ifPresent(u -> {
                throw new DuplicateUsernameException("Id : " + u.getUsername() + "??? ?????? ??????????????????.");
            });
    }

    public List<UserAdminPageDto> findUserInAdminPage() {
        List<User> users = userRepository.findAllByAttendStatus(AttendStatus.NOT_PARTICIPATED);

        List<UserAdminPageDto> userReponseDtoDatas = users.stream()
                .map((u) ->
                    UserAdminPageDto.builder()
                            .apiId(u.getApiId())
                            .attendStatus(u.getAttendStatus())
                            .nickname(u.getNickname())
                            .build())
                .collect(Collectors.toList());


        return userReponseDtoDatas;
    }



    public ListResponse<UserResponseDto> findUserList(Long offset, Long size) {

        List<User> users = userCustomRepository.findAllAndSortAndPage(offset, size);
        log.info("users : {}", users.size());
        List<User> participateUser = new LinkedList<>();
        List<User> notParticipateuser = new LinkedList<>();
        for (User x : users) {
            if (x.getAttendStatus().equals(AttendStatus.PARTICIPATED)) {
                participateUser.add(x);
            } else if (x.getAttendStatus().equals(AttendStatus.NOT_PARTICIPATED)) {
                notParticipateuser.add(x);
            }
        }
        log.info("notParticipateUser : {}", notParticipateuser .size());
        List<UserResponseDto> userResponseDtoDatas = participateUser.stream()
            .map((u) ->
                UserResponseDto.builder()
                    .id(u.getApiId())
                    .attendStatus(u.getAttendStatus())
                    .nickname(u.getNickname())
                    .role(userRoleRepository.currentlyUsedUserRole(u.getId())
                        .get(0)
                        .getRole()
                        .getValue())
                    .team(userTeamRepository.currentlyUsedUserTeam(u.getId())
                        .get(0)
                        .getTeam()
                        .getValue())
                    .vacation(u.getVacations().stream()
                            .max(comparingLong(Vacation::getId))
                            .orElse(Vacation.of(0D, 0D, 0D, "", u))
                            .getRemainingDays())
                        .attendanceScore(
                                dayStatisticalDataRepository.findAllByUser_idAndCalendar_id(u.getId(),
                                        calendarRepository.findByDate(LocalDate.now()).get().getId()).get().getAttendanceScore())
                        .totalScore(dayStatisticalDataRepository.findAllByUser_idAndCalendar_id(u.getId(),
                                calendarRepository.findByDate(LocalDate.now()).get().getId()).get().getTotalScore())
                        .weekAbsentScore(dayStatisticalDataRepository.findAllByUser_idAndCalendar_id(u.getId(),
                                calendarRepository.findByDate(LocalDate.now()).get().getId()).get().getWeekAbsentScore())
                    .build())
            .collect(Collectors.toList());
        List<UserResponseDto> userResponseDtoDatas2 = notParticipateuser.stream()
                .map((u) ->
                        UserResponseDto.builder()
                                .id(u.getApiId())
                                .attendStatus(u.getAttendStatus())
                                .nickname(u.getNickname())
                                .role(userRoleRepository.currentlyUsedUserRole(u.getId())
                                        .get(0)
                                        .getRole()
                                        .getValue())
                                .team(userTeamRepository.currentlyUsedUserTeam(u.getId())
                                        .get(0)
                                        .getTeam()
                                        .getValue())
                                .vacation(u.getVacations().stream()
                                        .max(comparingLong(Vacation::getId))
                                        .orElse(Vacation.of(0D, 0D, 0D, "", u))
                                        .getRemainingDays())
                                .attendanceScore((double)0)
                                .totalScore((double)0)
                                .weekAbsentScore((double)0)
                                .build())
                .collect(Collectors.toList());
        log.info("userResponseDtoDatas2 : {}", userResponseDtoDatas2.size());
        List<UserResponseDto> res = Stream.concat(userResponseDtoDatas.stream(), userResponseDtoDatas2.stream())
                .collect(Collectors.toList());
        return ListResponse.<UserResponseDto>builder()
            .count(res.size())
            .content(res)
            .build();
    }

    @Transactional
    public String changeTeam(String apiId, UserChangeTeamRequest request) {
        User user = userRepository.findByApiId(apiId).orElseThrow(() ->
            new EntityNotFoundException("apiId??? ???????????? user??? ????????????."));

        TeamEnum teamEnum = request.getTeam();
        Team team = teamRepository.findByValue(teamEnum).orElseThrow(() ->
            new EntityNotFoundException(teamEnum.name() + "??? ???????????? team??? ????????????."));

        List<UserTeam> originalUserTeams = userTeamRepository.currentlyUsedUserTeam(user.getId());

        user.originalUserTeamsCurrentlyUsedToFalse(originalUserTeams);
        UserTeam userTeam = UserTeam.createUserTeam(user, team, request.getReason(), true);
        userTeamRepository.save(userTeam);
        user.setNewCurrentlyUsedUserTeam(userTeam);
        return user.getApiId();
    }

    @Transactional
    public String changeRole(String apiId, UserChangeRoleRequest request) {
        User user = userRepository.findByApiId(apiId).orElseThrow(() ->
            new EntityNotFoundException("apiId??? ???????????? user??? ????????????."));
        RoleEnum roleEnum = request.getRole();

        Role role = roleRepository.findByValue(roleEnum).orElseThrow(() ->
            new EntityNotFoundException(roleEnum.name() + "??? ???????????? role??? ????????????."));
        List<UserRole> originalUserRoles = userRoleRepository.currentlyUsedUserRole(user.getId());

        user.originalUserRolesCurrentlyUsedToFalse(originalUserRoles);
        UserRole userRole = UserRole.createUserRole(user, role, request.getReason(), true);
        userRoleRepository.save(userRole);
        user.setNewCurrentlyUsedUserRole(userRole);
        authoritiesUpdater.update(user);
        return user.getApiId();
    }

    @Transactional
    public String changeAttendStatus(String apiId, UserChangeAttendStatusRequest request) {

        User user = userRepository.findByApiId(apiId).orElseThrow(() ->
            new EntityNotFoundException("apiId??? ???????????? user??? ????????????."));
        user.changeAttendStatus(request.getAttendStatus());

        if (request.getAttendStatus() == AttendStatus.PARTICIPATED) {
            Calendar calendar = calendarRepository.findByDate(LocalDate.now()).orElseThrow(() ->
                new EntityNotFoundException("?????? ????????? ???????????? calendar??? ????????????."));

            Optional<DayStatisticalData> dayStatisticalDataOptional = dayStatisticalDataRepository.findAllByUser_idAndCalendar_id(
                user.getId(), calendar.getId());
            Optional<Attendance> attendanceOptional = attendanceRepository.findByUserIdAndCalendarId(user.getId(), calendar.getId());

            if (attendanceOptional.isEmpty()) {
                Attendance attendance = Attendance.builder()
                    .user(user)
                    .calendar(calendar)
                    .build();
                attendanceRepository.save(attendance);
            }

            if (dayStatisticalDataOptional.isEmpty()) {
                DayStatisticalData dayStatisticalData = DayStatisticalData.createDayStatisticalData(user,
                    calendar);

                dayStatisticalDataRepository.save(dayStatisticalData);
            }
        } else if (request.getAttendStatus() == AttendStatus.NOT_PARTICIPATED) {
            Calendar calendar = calendarRepository.findByDate(LocalDate.now()).orElseThrow(() ->
                new EntityNotFoundException("?????? ????????? ???????????? calendar??? ????????????."));

            dayStatisticalDataRepository.findAllByUser_idAndCalendar_id(user.getId(), calendar.getId())
                .ifPresent((ddo) ->
                dayStatisticalDataRepository.delete(ddo));
            attendanceRepository.findByUserIdAndCalendarId(user.getId(), calendar.getId())
                .ifPresent((a) ->
                    attendanceRepository.delete(a));
        }
        return user.getApiId();
    }

    public ListResponse<UserParticipatingIdResponse> findParticipatingUserList() {

        List<UserParticipatingIdResponse> responseContents = userRepository.findAllByAttendStatus(
                AttendStatus.PARTICIPATED)
            .stream()
            .map((u) ->
                UserParticipatingIdResponse.builder()
                    .userId(u.getApiId())
                    .username(u.getUsername())
                    .build())
            .collect(Collectors.toList());
        return ListResponse.<UserParticipatingIdResponse>builder()
            .count(responseContents.size())
            .content(responseContents)
            .build();
    }

    @Transactional
    public void passwordInquery(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
            new EntityNotFoundException("username??? ???????????? user??? ????????????."));
        EmailAuth emailAuth = emailAuthRepository.save(
            EmailAuth.createEmailAuth(user.getEmail(), UUID.randomUUID().toString()));

        emailService.send(emailAuth.getEmail(),
            "42Seoul ????????? ?????? ???????????? ?????? ????????? ???????????????." +
                " ??????????????? ???????????? ?????? ??????????????? ??? ???????????? ???????????????." +
                " 2??? ??? ???????????????.",
            EmailAuth.HOST + EmailAuth.EMAILAUTHPATH + "?id=" + user.getApiId()
                + "&email=" + emailAuth.getEmail()
                + "&authToken=" + emailAuth.getAuthToken()
                + "&authorizationType=" + EmailAuthRequestDto.RESET);

    }

    @Transactional
    public void changePassword(String username, UserChangePasswordRequest request) {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
            new EntityNotFoundException("username??? ???????????? user??? ????????????."));
        if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new PasswordNotMatchException("?????? ??????????????? ???????????? ????????????.");
        }

        user.updatePassword(bCryptPasswordEncoder.encode(request.getPassword()));
    }
}
