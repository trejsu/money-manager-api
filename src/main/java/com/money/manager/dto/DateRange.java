package com.money.manager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.money.manager.common.validation.StartDateBeforeEndDate;
import lombok.Value;

import java.time.LocalDate;

import static java.time.LocalDate.parse;
import static org.springframework.util.StringUtils.isEmpty;

@Value
@StartDateBeforeEndDate
public class DateRange {

    private final static LocalDate MIN_DATE = parse("0000-01-01");
    private final static LocalDate MAX_DATE = parse("9999-12-31");

    LocalDate start;
    LocalDate end;

    @JsonCreator
    public DateRange(@JsonProperty("start") LocalDate start, @JsonProperty("end") LocalDate end) {
        this.start = isEmpty(start) ? MIN_DATE : start;
        this.end = isEmpty(end) ? MAX_DATE : end;
    }

    public DateRange(String start, String end) {
        this.start = isEmpty(start) ? MIN_DATE : parse(start);
        this.end = isEmpty(end) ? MAX_DATE : parse(end);
    }

    public boolean containsDate(String date) {
        final LocalDate parsedDate = parse(date);
        return containsDate(parsedDate);
    }

    public boolean containsDate(LocalDate date) {
        return !(date.isBefore(start) || date.isAfter(end));
    }

    public static DateRange withoutBounds() {
        return new DateRange((LocalDate) null, null);
    }
}
