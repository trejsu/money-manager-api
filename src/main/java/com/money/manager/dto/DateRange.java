package com.money.manager.dto;

import com.money.manager.common.validation.StartDateBeforeEndDate;
import lombok.Value;

import java.time.LocalDate;

import static org.springframework.util.StringUtils.isEmpty;

@Value
@StartDateBeforeEndDate
public class DateRange {

    private final static String MIN_DATE = "0000-01-01";
    private final static String MAX_DATE = "9999-12-31";

    private String start;
    private String end;

    public DateRange(String start, String end) {
        this.start = isEmpty(start) ? MIN_DATE : start;
        this.end = isEmpty(end) ? MAX_DATE : end;
    }

    public boolean containsDate(String date) {
        final LocalDate parsedDate = LocalDate.parse(date);
        final LocalDate startDate = LocalDate.parse(start);
        final LocalDate endDate = LocalDate.parse(end);
        return !(parsedDate.isBefore(startDate) || parsedDate.isAfter(endDate));
    }

    public static DateRange withoutBounds() {
        return new DateRange(null, null);
    }
}
