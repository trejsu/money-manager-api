package com.money.manager.common.validation;


import com.money.manager.dto.DateRange;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = StartDateBeforeEndDate.StartDateBeforeEndDateValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface StartDateBeforeEndDate {
    String message() default "Start date must be before end date.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class StartDateBeforeEndDateValidator implements ConstraintValidator<StartDateBeforeEndDate, DateRange> {

        @Override
        public void initialize(StartDateBeforeEndDate startDateBeforeEndDate) {

        }

        @Override
        public boolean isValid(DateRange dateRange, ConstraintValidatorContext constraintValidatorContext) {
            return dateRange.getStart().isBefore(dateRange.getEnd());
        }
    }
}
