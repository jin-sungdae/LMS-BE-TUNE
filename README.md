

# Description

- 성능 튜닝을 위한 코드 Refactoring
- JPA에 대한 이해와 쿼리, Stream을 이용해서 db 접근을 최소화하는 방식으로 진행

```java
    public void checkIn{
        Stream<AttendanceStatus> checkOut=attendanceRepository.findAttendanceByUserId(findAttendanceOptional.get().getUser().getId())

        Stream<AttendanceStatus> checkIn=attendanceRepository.findAttendanceByUserId(findAttendanceOptional.get().getUser().getId())


        Stream<AttendanceStatus> checkOut2=attendanceRepository.findAttendanceByUserId(user.get().getId())

        Stream<AttendanceStatus> checkIn2=attendanceRepository.findAttendanceByUserId(user.get().getId())
    }
    
    public void checkOut {
        Stream<AttendanceStatus> checkOut = attendanceRepository.findAttendanceByUserId(user.get().getId())
       

        Stream<AttendanceStatus> checkIn = attendanceRepository.findAttendanceByUserId(user.get().getId())
        
        Stream<AttendanceStatus> checkOut2 = attendanceRepository.findAttendanceByUserId(user.get().getId())
        
        Stream<AttendanceStatus> checkIn2 = attendanceRepository.findAttendanceByUserId(user.get().getId())
    }
    
```
- Stream으로 Functional Programming을 하려는 시도를 하였는데 Stream으로 만들때마다 Jpa로 데이터를 계속 뽑아오는 행위를 하였다.
- Jpa로 뽑아오는 Stream 은 결국 같은 데이터를 뽑아오는데 중복으로 여러번 뽑아오는 시도를 하는 것
```java
    Stream<Attendance> checkInAndOutStatus = attendanceRepository.findAttendanceByUserId(findAttendanceOptional.get().getUser().getId());
    final LocalDate today = findAttendanceOptional.get().getCalendar().getDate();
    List<AttendanceStatus> checkInList = checkStatusList(checkInAndOutStatus, today, today.getMonth(), NONE);

    final LocalDate date = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SATURDAY));
    List<AttendanceStatus> checkInList2 = checkStatusList(checkInAndOutStatus, date, today.getMonth(), ABSENT);
```
- 중복 코드로 판단하여 중복코드를 제거 하였다.
- CheckIn 함수와 CheckOut함수에 있는 출석 상태 관리하기 위한 코드들이 중복되는 것을 확인
```java
 public  List<AttendanceStatus> checkStatusList(Stream<Attendance> checkInAndOutStatus, LocalDate date, Month month, AttendanceStatus status) {
        List<AttendanceStatus> checkOutList2 = checkInAndOutStatus
                .filter(x -> x.getCheckOutStatus() == status
                        && x.getCalendar().getDate().getMonth().equals(month)
                        && x.getCalendar().getDate().isAfter(date))
                .map(Attendance::getCheckOutStatus)
                .collect(Collectors.toList());

        List<AttendanceStatus> checkInList2  = checkInAndOutStatus
                .filter(x -> x.getCheckInStatus() == status
                        && x.getCalendar().getDate().getMonth().equals(month)
                        && x.getCalendar().getDate().isAfter(date))
                .map(Attendance::getCheckInStatus)
                .collect(Collectors.toList());
        checkInList2.addAll(checkOutList2);
        return checkInList2;
    }
```
- 함수로 만들어서 기능 분리를 시켜주었다.