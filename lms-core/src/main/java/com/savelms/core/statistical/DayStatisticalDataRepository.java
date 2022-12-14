package com.savelms.core.statistical;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DayStatisticalDataRepository extends JpaRepository<DayStatisticalData, Long> {

    List<DayStatisticalData> findByuser_idAndCalendar_id(Long id, Long calendar_id);

    boolean existsByuser_idAndCalendar_id(Long id, Long calendar_id);

    Optional<DayStatisticalData> findAllByUser_idAndCalendar_id(Long aLong, Long calendar_id);

    @Query("select distinct d from DayStatisticalData d " +
            "join fetch d.user u " +
            "join fetch u.userRoles ur " +
            "join fetch ur.role r " +
            "where d.calendar.date =:date order by u.id asc")
    List<DayStatisticalData> findAllByDate(@Param("date") LocalDate date);

    @Query("select distinct d from DayStatisticalData d " +
            "join fetch d.user u " +
            "join fetch u.userRoles ur " +
            "join fetch ur.role r " +
            "where d.calendar.date =:date " +
            "order by u.id asc")
    List<DayStatisticalData> findAllByDateAndAttendStatus(
            @Param("date") LocalDate date);


    @Query("select d from DayStatisticalData d where d.user.apiId = :apiId and d.calendar.date = :date")
    Optional<DayStatisticalData> findByApiIdAndDate(@Param("apiId") String apiId, @Param("date") LocalDate date);

}
