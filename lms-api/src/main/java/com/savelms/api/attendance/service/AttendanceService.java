package com.savelms.api.attendance.service;

import com.savelms.core.attendance.domain.AttendanceStatus;
import com.savelms.core.attendance.domain.entity.Attendance;
import com.savelms.core.attendance.dto.AttendanceDto;
import com.savelms.core.user.AttendStatus;

import java.time.LocalDate;
import java.util.Map;

public interface AttendanceService {
    void checkIn(Long attendanceId, String apiId, AttendanceStatus status);

    void checkOut(Long attendanceId, String apiId, AttendanceStatus status);

    Map<Long, AttendanceDto> getAllAttendanceByDate(LocalDate date);

    Map<Long, Attendance> getAllAttendanceByDateAndAttendStatus(LocalDate date, AttendStatus attendStatus);
}
