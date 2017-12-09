package com.money.manager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TimePeriod {
    private String start;
    private String end;

    public TimePeriod(String start, String end) {
        initializeStart(start);
        initializeEnd(end);
    }

    private void initializeEnd(String end) {
        if (end == null || end.isEmpty()) {
            this.end = "9999-99-99";
        } else {
            this.end = end;
        }
    }

    private void initializeStart(String start) {
        if (start == null || start.isEmpty()) {
            this.start = "0000-00-00";
        } else {
            this.start = start;
        }
    }
}
