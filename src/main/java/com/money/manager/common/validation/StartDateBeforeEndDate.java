package com.money.manager.common.validation;


import com.money.manager.dto.DateRange;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Documented
@Constraint(validatedBy = StartDateBeforeEndDate.StartDateBeforeEndDateValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface StartDateBeforeEndDate {
    String message() default "Start date must be before end date.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class StartDateBeforeEndDateValidator implements ConstraintValidator<StartDateBeforeEndDate, DateRange> {

        private final static DateTimeFormatter DATE_TIME_FORMATTER = ISO_LOCAL_DATE;

        @Override
        public void initialize(StartDateBeforeEndDate startDateBeforeEndDate) {

        }

        @Override
        public boolean isValid(DateRange dateRange, ConstraintValidatorContext constraintValidatorContext) {
            LocalDate start = LocalDate.parse(dateRange.getStart(), DATE_TIME_FORMATTER);
            LocalDate end = LocalDate.parse(dateRange.getEnd(), DATE_TIME_FORMATTER);
            return start.isBefore(end);
        }
    }
}
