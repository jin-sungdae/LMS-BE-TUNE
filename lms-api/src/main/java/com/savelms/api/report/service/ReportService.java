package com.savelms.api.report.service;

import com.savelms.api.report.dto.ReportResponse;
import com.savelms.core.monthreport.MonthReport;
import com.savelms.core.monthreport.MonthReportRepository;
import com.savelms.core.weekreport.WeekReport;
import com.savelms.core.weekreport.WeekReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {

    private final WeekReportRepository weekReportRepository;
    private final MonthReportRepository monthReportRepository;



    public List<ReportResponse> getDagerUser() {
        List<WeekReport> reportList = weekReportRepository.findAll();

        return reportList.stream()
                .map(ReportResponse::new)
                .collect(Collectors.toList());
    }


    public List<MonthReport> getGradeUser(LocalDate date) {
        List<MonthReport> reportStream = monthReportRepository.findAll()
                .stream().filter(x -> x.getMonth().getMonth().equals(date.getMonth()))
                .collect(Collectors.toList());
        return reportStream;
    }
}
