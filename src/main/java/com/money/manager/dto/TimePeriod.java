package com.money.manager.dto;

import com.money.manager.common.validation.StartDateBeforeEndDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static org.springframework.util.StringUtils.isEmpty;

@NoArgsConstructor
@Getter
@StartDateBeforeEndDate
public class TimePeriod {

    private final static String MIN_DATE = "0000-01-01";
    private final static String MAX_DATE = "9999-12-31";

    private String start;
    private String end;

    public TimePeriod(String start, String end) {
        this.start = isEmpty(start) ? MIN_DATE : start;
        this.end = isEmpty(end) ? MAX_DATE : end;
    }

    public void setStart(String start) {
        this.start = isEmpty(start) ? MIN_DATE : start;
    }

    public void setEnd(String end) {
        this.end = isEmpty(end) ? MAX_DATE : end;
    }

    public boolean containsDate(String date) {
        final LocalDate parsedDate = LocalDate.parse(date);
        final LocalDate startDate = LocalDate.parse(start);
        final LocalDate endDate = LocalDate.parse(end);
        return !(parsedDate.isBefore(startDate) || parsedDate.isAfter(endDate));
    }
}
