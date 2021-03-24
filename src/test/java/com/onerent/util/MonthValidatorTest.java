package com.onerent.util;

import com.onerent.hostels.HostelReservationService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class MonthValidatorTest {

    @ParameterizedTest
    @ValueSource(ints = {-3,0, 15, Integer.MAX_VALUE})
    public void it_should_throw_error_when_month_is_invalid(int month){
        assertThrows(java.time.DateTimeException.class,() -> new MonthValidator().validateMonth(month));
    }

    @ParameterizedTest
    @EnumSource(Month.class)
    public void it_should_not_throw_exceptions_for_valid_month(Month month){
        assertDoesNotThrow(() -> new MonthValidator().validateMonth(month.getValue()));
    }

}
