package com.groupeonepoint.onerent.util;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class MonthValidator {
    /**
     * Will throw {@link java.time.DateTimeException} if the month is invalid
     * @param month
     */
    public void validateMonth(int month){
        LocalDate.of(1970, month, 1);
    }

}
