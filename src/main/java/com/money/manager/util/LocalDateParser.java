package com.money.manager.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateParser {

    private DateTimeFormatter dateTimeFormatter;

    public LocalDateParser(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public String parseToString(LocalDate localDate) {
        return localDate.format(dateTimeFormatter);
    }

    public LocalDate parseFromString(String string) {
        return LocalDate.parse(string, dateTimeFormatter);
    }
}
