package com.savelms.core.calendar.domain.entity;

import com.savelms.core.BaseEntity;
import com.savelms.core.attendance.domain.entity.Attendance;
import com.savelms.core.calendar.DayType;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "CALENDAR", uniqueConstraints = @UniqueConstraint(name = "DATE_UNIQUE", columnNames = {
    "date"}))

@Entity
@Builder
public class Calendar extends BaseEntity {

    //********************************* static final 상수 필드 *********************************/

    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CALENDAR_ID")
    private Long id;

    /********************************* PK가 아닌 필드 *********************************/

    LocalDate date;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    DayType dayType;

    /********************************* 비영속 필드 *********************************/


    /********************************* 연관관계 매핑 *********************************/

    @Singular
    @OneToMany(mappedBy = "calendar")
    private final List<Attendance> attendances = new ArrayList<>();

    /********************************* 비니지스 로직 *********************************/


    public void changeDayType(DayType dayType) {
        this.dayType = dayType;
    }

    public boolean isStudyDay() {
        if (this.dayType == DayType.STUDYDAY) {
            return true;
        } else {
            return false;
        }
    }

    public Calendar(LocalDate now, DayType holiday) {
        this.date = now;
        this.dayType = holiday;
    }
}
