package ar.edu.utn.frc.tup.lc.iv.services.interfaces.billExpense;

import ar.edu.utn.frc.tup.lc.iv.dtos.billExpense.PeriodDto;
/**
 * Service manager validation of period for BillRecord.
 */
public interface IPeriodBillExpenseValidation {
    /**
     * Validates the given period.
     *
     * @param period the period to be validated
     */
    void validatePeriod(PeriodDto period);
}
