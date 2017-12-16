package com.money.manager.dto;

import com.money.manager.common.validation.StartDateBeforeEndDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.springframework.util.StringUtils.isEmpty;

@NoArgsConstructor
@Getter
@Setter
@StartDateBeforeEndDate
public class TimePeriod {

    private final static String MIN_DATE = "0000-00-00";
    private final static String MAX_DATE = "9999-99-99";

    private String start;
    private String end;

    public TimePeriod(String start, String end) {
        this.start = isEmpty(start) ? MIN_DATE : start;
        this.end = isEmpty(end) ? MAX_DATE : end;
    }
}
