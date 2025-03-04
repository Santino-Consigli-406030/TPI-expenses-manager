package ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense;

import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
import ar.edu.utn.frc.tup.lc.iv.services.impl.billExpense.PeriodBillExpenseValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class PeriodBillExpenseValidationTest {

    private PeriodBillExpenseValidation periodBillExpenseValidation;

    @BeforeEach
    public void setUp() {
        periodBillExpenseValidation = new PeriodBillExpenseValidation();
    }

    @Test
    public void testValidatePeriod_NullPeriodDto() {
        assertThrows(CustomException.class, () -> periodBillExpenseValidation.validatePeriod(null));
    }

    @Test
    public void testValidatePeriod_NullStartDate() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setEndDate(LocalDate.now().minusDays(1));
        assertThrows(CustomException.class, () -> periodBillExpenseValidation.validatePeriod(periodDto));
    }

    @Test
    public void testValidatePeriod_NullEndDate() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.now().minusDays(2));
        assertThrows(CustomException.class, () -> periodBillExpenseValidation.validatePeriod(periodDto));
    }

    @Test
    public void testValidatePeriod_StartDateAfterEndDate() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.now().minusDays(1));
        periodDto.setEndDate(LocalDate.now().minusDays(2));
        assertThrows(CustomException.class, () -> periodBillExpenseValidation.validatePeriod(periodDto));
    }

    @Test
    public void testValidatePeriod_StartDateNotInPast() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.now());
        periodDto.setEndDate(LocalDate.now().minusDays(1));
        assertThrows(CustomException.class, () -> periodBillExpenseValidation.validatePeriod(periodDto));
    }

    @Test
    public void testValidatePeriod_EndDateNotInPast() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.now().minusDays(2));
        periodDto.setEndDate(LocalDate.now());
        assertThrows(CustomException.class, () -> periodBillExpenseValidation.validatePeriod(periodDto));
    }

    @Test
    public void testValidatePeriod_StartDateEqualsEndDate() {
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(LocalDate.now().minusDays(1));
        periodDto.setEndDate(LocalDate.now().minusDays(1));
        assertThrows(CustomException.class, () -> periodBillExpenseValidation.validatePeriod(periodDto));
    }
}
