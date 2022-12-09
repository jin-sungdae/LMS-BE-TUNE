package com.savelms.api.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthReportResponse {
    String UserName;
    String UserNickName;
    int grade;
}
