package com.savelms.api.statistical.service;

import com.savelms.api.attendance.service.AttendanceService;
import com.savelms.api.statistical.dto.DayLogDto;
import com.savelms.api.statistical.dto.DayStatisticalDataDto;
import com.savelms.api.todo.service.TodoService;
import com.savelms.api.user.userrole.service.UserRoleService;
import com.savelms.api.user.userteam.service.UserTeamService;
import com.savelms.api.vacation.service.VacationService;
import com.savelms.core.attendance.domain.AttendanceStatus;
import com.savelms.core.attendance.domain.entity.Attendance;
import com.savelms.core.attendance.dto.AttendanceDto;
import com.savelms.core.statistical.DayStatisticalData;
import com.savelms.core.statistical.DayStatisticalDataRepository;
import com.savelms.core.team.TeamEnum;
import com.savelms.core.user.AttendStatus;
import com.savelms.core.user.domain.entity.User;
import com.savelms.core.user.role.RoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class DayStatisticalDataService {

    private final TodoService todoService;
    private final VacationService vacationService;
    private final UserRoleService userRoleService;
    private final UserTeamService userTeamService;
    private final AttendanceService attendanceService;
    private final DayStatisticalDataRepository statisticalDataRepository;


    public List<DayLogDto> getDayLogs(LocalDate date, AttendStatus attendStatus) {
        if (attendStatus == null) {
            return getDayLogsByDate(date);
        }
        return getDayLogsByDateAndAttendStatus(date, attendStatus);
    }


    private List<DayLogDto> getDayLogsByDate(LocalDate date) {
        List<DayLogDto> list1 = getDayLogsByDateAndAttendStatus(date, AttendStatus.PARTICIPATED);
        List<DayLogDto> list2 = getDayLogsByDateAndAttendStatus(date, AttendStatus.NOT_PARTICIPATED);

        List<DayLogDto> mergedList = new LinkedList<>();
        mergedList.addAll(list1);
        mergedList.addAll(list2);

        return mergedList;
    }


    private List<DayLogDto> getDayLogsByDateAndAttendStatus(LocalDate date, AttendStatus attendStatus) {
        final Map<Long, Attendance> attendances = attendanceService.getAllAttendanceByDateAndAttendStatus(date, attendStatus);

        final Map<Long, TeamEnum> teams = userTeamService.findAllUserTeamByDateAndAttendStatus(date);
        final Map<Long, RoleEnum> roles = userRoleService.findAllUserRoleByDateAndAttendStatus(date);
        final Map<Long, Double> remainingVacations = vacationService.getRemainingVacationByDateAndAttendStatus(date);
        final Map<String, Double> todoProgress = todoService.getTodoProgressAndAttendStatus(date);
        final List<DayStatisticalData> dayStatisticalData = statisticalDataRepository.findAllByDateAndAttendStatus(date);
        Map<Long, DayStatisticalDataDto> dayStatisticalDataDtoMap = dayStatisticalData.stream()
            .collect(
                Collectors.toMap((d) -> d.getUser().getId(), d -> DayStatisticalDataDto.from(d)));
        return attendances.entrySet()
            .stream()
            .map((e) -> (
                DayLogDto.builder()
                    .tableDay(date)
                    .userId(e.getValue().getUser().getApiId())
                    .attendanceId(e.getValue().getId())
                    .username(e.getValue().getUser().getNickname())
                    .role(roles.get(e.getValue().getUser().getId()))
                    .team(teams.get(e.getValue().getUser().getId()))
                    .vacation(remainingVacations.get(e.getValue().getUser().getId()))
                    .attendStatus(e.getValue().getAttendStatus())
                    .checkIn(e.getValue().getCheckInStatus())
                    .checkOut(e.getValue().getCheckOutStatus())
                    .todoSuccessRate(todoProgress.get(e.getValue().getUser().getApiId()))
                    .weekAbsentScore(dayStatisticalDataDtoMap.get(e.getValue().getUser().getId())
                        .getWeekAbsentScore())
                    .attendanceScore(dayStatisticalDataDtoMap.get(e.getValue().getUser().getId())
                        .getAttendanceScore())
                    .totalAbsentScore(dayStatisticalDataDtoMap.get(e.getValue().getUser().getId())
                        .getTotalScore())
                    .build()
            ))
            .collect(Collectors.toList());

    }


    public void updateStudyTimeScore(String apiId, LocalDate date, Double score) {
        statisticalDataRepository.findByApiIdAndDate(apiId, date)
                .ifPresentOrElse(curStatData -> curStatData.increaseAndDecreaseStudyTimeScore(score),
                        () -> log.warn("[study_time] - 통계 데이터가 존재하지 않습니다."));
    }
}
