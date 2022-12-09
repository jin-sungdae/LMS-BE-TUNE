package com.savelms.core.exception.study_time;

import com.savelms.core.exception.ExceptionStatus;
import lombok.Getter;

@Getter
public class StudyTimeException extends RuntimeException {

    private final ExceptionStatus exceptionStatus;

    public StudyTimeException(ExceptionStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

}
